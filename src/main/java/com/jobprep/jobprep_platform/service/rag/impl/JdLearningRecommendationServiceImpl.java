package com.jobprep.jobprep_platform.service.rag.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobprep.jobprep_platform.annotation.NeedLogin;
import com.jobprep.jobprep_platform.config.ai.AiProperties;
import com.jobprep.jobprep_platform.config.rag.RagProperties;
import com.jobprep.jobprep_platform.mapper.NoteMapper;
import com.jobprep.jobprep_platform.mapper.QuestionMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.rag.JdLearningRecommendationRequest;
import com.jobprep.jobprep_platform.model.dto.rag.RebuildLearningResourceIndexRequest;
import com.jobprep.jobprep_platform.model.entity.Note;
import com.jobprep.jobprep_platform.model.entity.Question;
import com.jobprep.jobprep_platform.model.entity.rag.LearningResourceDocument;
import com.jobprep.jobprep_platform.model.entity.rag.LearningResourceHit;
import com.jobprep.jobprep_platform.model.vo.rag.JdLearningRecommendationVO;
import com.jobprep.jobprep_platform.model.vo.rag.LearningResourceHitVO;
import com.jobprep.jobprep_platform.model.vo.rag.RebuildLearningResourceIndexVO;
import com.jobprep.jobprep_platform.service.rag.EmbeddingClient;
import com.jobprep.jobprep_platform.service.rag.JdLearningRecommendationService;
import com.jobprep.jobprep_platform.service.rag.LearningResourceVectorStore;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.resumematch.HashUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
public class JdLearningRecommendationServiceImpl implements JdLearningRecommendationService {
    private static final String RESOURCE_TYPE_NOTE = "NOTE";
    private static final String RESOURCE_TYPE_QUESTION = "QUESTION";
    private static final String RECOMMENDATION_CACHE_KEY = "rag:jd-recommendation:%s:%d";
    private static final int MAX_CONTEXT_CHARS = 7000;
    private static final Pattern SKILL_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9+#.\\-]{2,}");
    private static final Set<String> STOP_WORDS = Set.of(
            "and", "the", "with", "for", "you", "our", "are", "will", "from", "that",
            "this", "have", "has", "job", "role", "team", "years", "experience", "skills",
            "ability", "including", "using", "work", "build", "design"
    );

    private final NoteMapper noteMapper;
    private final QuestionMapper questionMapper;
    private final EmbeddingClient embeddingClient;
    private final LearningResourceVectorStore vectorStore;
    private final RagProperties ragProperties;
    private final AiProperties aiProperties;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;

    @Override
    @NeedLogin
    public ApiResponse<RebuildLearningResourceIndexVO> rebuildIndex(RebuildLearningResourceIndexRequest request) {
        try {
            int noteLimit = request.getNoteLimit() == null ? 500 : request.getNoteLimit();
            int questionLimit = request.getQuestionLimit() == null ? 500 : request.getQuestionLimit();

            List<Question> questions = questionMapper.findAllForRagIndex(questionLimit);
            int indexedQuestions = 0;
            for (Question question : questions) {
                LearningResourceDocument document = questionDocument(question);
                vectorStore.upsert(document, embeddingClient.embed(document.getContent()));
                indexedQuestions++;
            }

            List<Note> notes = noteMapper.findAllForRagIndex(noteLimit);
            Map<Integer, Question> questionMap = loadQuestionMapForNotes(notes);
            int indexedNotes = 0;
            for (Note note : notes) {
                LearningResourceDocument document = noteDocument(note, questionMap.get(note.getQuestionId()));
                vectorStore.upsert(document, embeddingClient.embed(document.getContent()));
                indexedNotes++;
            }

            return ApiResponseUtil.success("success", RebuildLearningResourceIndexVO.builder()
                    .indexedNotes(indexedNotes)
                    .indexedQuestions(indexedQuestions)
                    .totalIndexed(indexedNotes + indexedQuestions)
                    .build());
        } catch (Exception e) {
            log.error("failed to rebuild RAG index", e);
            return ApiResponseUtil.error("failed to rebuild RAG index: " + e.getMessage());
        }
    }

    @Override
    @NeedLogin
    public ApiResponse<JdLearningRecommendationVO> recommend(JdLearningRecommendationRequest request) {
        int topK = request.getTopK() == null ? ragProperties.getRetrieval().getTopK() : request.getTopK();
        String cacheKey = RECOMMENDATION_CACHE_KEY.formatted(HashUtils.sha256(request.getJdText()), topK);
        JdLearningRecommendationVO cached = readCached(cacheKey);
        if (cached != null) {
            cached.setCached(true);
            return ApiResponseUtil.success("success", cached);
        }

        try {
            double[] queryEmbedding = embeddingClient.embed(request.getJdText());
            List<LearningResourceHit> hits = vectorStore.search(queryEmbedding, topK);

            JdLearningRecommendationVO vo = new JdLearningRecommendationVO();
            vo.setSkillKeywords(extractSkillKeywords(request.getJdText()));
            vo.setResources(hits.stream().map(this::toVO).toList());
            vo.setRoadmap(generateRoadmap(request.getJdText(), hits, vo.getSkillKeywords()));
            vo.setCached(false);

            stringRedisTemplate.opsForValue().set(
                    cacheKey,
                    objectMapper.writeValueAsString(vo),
                    ragProperties.getRetrieval().getCacheTtlMinutes(),
                    TimeUnit.MINUTES
            );
            return ApiResponseUtil.success("success", vo);
        } catch (Exception e) {
            log.error("failed to generate JD learning recommendation", e);
            return ApiResponseUtil.error("failed to generate JD learning recommendation: " + e.getMessage());
        }
    }

    private Map<Integer, Question> loadQuestionMapForNotes(List<Note> notes) {
        List<Integer> questionIds = notes.stream()
                .map(Note::getQuestionId)
                .distinct()
                .toList();
        if (questionIds.isEmpty()) {
            return Map.of();
        }
        return questionMapper.findByIdBatch(questionIds).stream()
                .collect(Collectors.toMap(Question::getQuestionId, question -> question));
    }

    private LearningResourceDocument questionDocument(Question question) throws JsonProcessingException {
        String content = """
                Interview question: %s
                Exam point: %s
                Difficulty: %s
                """.formatted(
                nullToEmpty(question.getTitle()),
                nullToEmpty(question.getExamPoint()),
                question.getDifficulty() == null ? "unknown" : question.getDifficulty()
        );
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("categoryId", question.getCategoryId());
        metadata.put("difficulty", question.getDifficulty());
        return LearningResourceDocument.builder()
                .resourceType(RESOURCE_TYPE_QUESTION)
                .resourceId(question.getQuestionId().longValue())
                .title(question.getTitle())
                .content(content)
                .metadataJson(objectMapper.writeValueAsString(metadata))
                .build();
    }

    private LearningResourceDocument noteDocument(Note note, Question question) throws JsonProcessingException {
        String questionTitle = question == null ? "Unknown question" : question.getTitle();
        String content = """
                Note for question: %s
                Note content:
                %s
                """.formatted(questionTitle, nullToEmpty(note.getContent()));
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("questionId", note.getQuestionId());
        metadata.put("authorId", note.getAuthorId());
        return LearningResourceDocument.builder()
                .resourceType(RESOURCE_TYPE_NOTE)
                .resourceId(note.getNoteId().longValue())
                .title("Note: " + questionTitle)
                .content(content)
                .metadataJson(objectMapper.writeValueAsString(metadata))
                .build();
    }

    private JdLearningRecommendationVO readCached(String cacheKey) {
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached == null || cached.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(cached, JdLearningRecommendationVO.class);
        } catch (Exception e) {
            log.warn("failed to parse cached RAG recommendation", e);
            stringRedisTemplate.delete(cacheKey);
            return null;
        }
    }

    private LearningResourceHitVO toVO(LearningResourceHit hit) {
        LearningResourceHitVO vo = new LearningResourceHitVO();
        vo.setResourceType(hit.getResourceType());
        vo.setResourceId(hit.getResourceId());
        vo.setTitle(hit.getTitle());
        vo.setSimilarity(hit.getSimilarity());
        vo.setSnippet(snippet(hit.getContent()));
        return vo;
    }

    private String generateRoadmap(String jdText, List<LearningResourceHit> hits, List<String> skills) {
        try {
            return callOllamaForRoadmap(jdText, hits, skills);
        } catch (Exception e) {
            log.warn("Ollama roadmap generation failed; using deterministic fallback", e);
            return fallbackRoadmap(skills, hits);
        }
    }

    private String callOllamaForRoadmap(String jdText, List<LearningResourceHit> hits, List<String> skills) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        int timeoutMs = Math.toIntExact(Duration.ofSeconds(aiProperties.getOllama().getTimeoutSeconds()).toMillis());
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);

        RestClient restClient = RestClient.builder()
                .baseUrl(aiProperties.getOllama().getBaseUrl())
                .requestFactory(requestFactory)
                .build();
        OllamaGenerateResponse response = restClient.post()
                .uri("/api/generate")
                .body(new OllamaGenerateRequest(
                        aiProperties.getOllama().getModel(),
                        buildRoadmapPrompt(jdText, hits, skills),
                        false
                ))
                .retrieve()
                .body(OllamaGenerateResponse.class);
        if (response == null || response.response() == null || response.response().isBlank()) {
            throw new IllegalStateException("Ollama returned an empty roadmap");
        }
        return response.response().trim();
    }

    private String buildRoadmapPrompt(String jdText, List<LearningResourceHit> hits, List<String> skills) {
        String context = hits.stream()
                .map(hit -> "- [%s #%d] %s\n%s".formatted(
                        hit.getResourceType(),
                        hit.getResourceId(),
                        hit.getTitle(),
                        snippet(hit.getContent(), 900)
                ))
                .collect(Collectors.joining("\n"));
        if (context.length() > MAX_CONTEXT_CHARS) {
            context = context.substring(0, MAX_CONTEXT_CHARS);
        }
        return """
                You are a backend interview coach. Based on the job description and retrieved internal learning resources,
                create a concise learning roadmap for a Java backend candidate in the US job market.

                Requirements:
                - Mention priority skills first.
                - Recommend the retrieved resources by type and id.
                - Keep it actionable and concise.
                - Do not invent resources outside the provided context.

                JD:
                %s

                Extracted skills:
                %s

                Retrieved internal resources:
                %s
                """.formatted(jdText, String.join(", ", skills), context);
    }

    private String fallbackRoadmap(List<String> skills, List<LearningResourceHit> hits) {
        String resources = hits.stream()
                .map(hit -> "%s #%d: %s".formatted(hit.getResourceType(), hit.getResourceId(), hit.getTitle()))
                .collect(Collectors.joining("\n"));
        return """
                Priority roadmap:
                1. Strengthen core backend skills from the JD: %s.
                2. Review the matched internal notes/questions below and rewrite your project bullets with concrete Java, Spring Boot, database, cache, messaging, and cloud evidence.
                3. Build one project story that connects API design, async processing, Redis cost control, and deployment.

                Recommended resources:
                %s
                """.formatted(String.join(", ", skills), resources);
    }

    private List<String> extractSkillKeywords(String jdText) {
        Matcher matcher = SKILL_PATTERN.matcher(jdText == null ? "" : jdText);
        Set<String> skills = new LinkedHashSet<>();
        while (matcher.find()) {
            String value = matcher.group();
            String normalized = value.toLowerCase(Locale.ROOT);
            if (!STOP_WORDS.contains(normalized)) {
                skills.add(value);
            }
            if (skills.size() >= 20) {
                break;
            }
        }
        return new ArrayList<>(skills);
    }

    private String snippet(String value) {
        return snippet(value, 280);
    }

    private String snippet(String value, int limit) {
        String normalized = nullToEmpty(value).replaceAll("\\s+", " ").trim();
        if (normalized.length() <= limit) {
            return normalized;
        }
        return normalized.substring(0, limit) + "...";
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    private record OllamaGenerateRequest(String model, String prompt, boolean stream) {
    }

    @com.fasterxml.jackson.annotation.JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaGenerateResponse(String response) {
    }
}
