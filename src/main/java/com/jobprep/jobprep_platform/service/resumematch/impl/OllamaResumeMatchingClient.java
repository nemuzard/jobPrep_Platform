package com.jobprep.jobprep_platform.service.resumematch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobprep.jobprep_platform.config.ai.AiProperties;
import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchAnalysisResult;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Log4j2
@Service
@RequiredArgsConstructor
public class OllamaResumeMatchingClient implements ResumeMatchingClient {
    private static final int MAX_RESUME_CHARS = 12000;
    private static final int MAX_JD_CHARS = 8000;
    private static final Pattern TOKEN_PATTERN = Pattern.compile("[A-Za-z][A-Za-z0-9+#.\\-]{1,}");
    private static final Set<String> STOP_WORDS = Set.of(
            "and", "or", "the", "with", "for", "you", "our", "are", "will", "from",
            "that", "this", "have", "has", "your", "team", "work", "role", "job",
            "years", "experience", "skills", "ability", "including", "using"
    );

    private final AiProperties aiProperties;
    private final ObjectMapper objectMapper;

    @Override
    public ResumeMatchAnalysisResult score(String resumeText, String jdText) {
        if (!"ollama".equalsIgnoreCase(aiProperties.getProvider())) {
            return heuristicScore(resumeText, jdText);
        }
        try {
            ResumeMatchAnalysisResult result = callOllama(resumeText, jdText);
            normalize(result);
            return result;
        } catch (Exception e) {
            log.warn("Ollama scoring failed; falling back to deterministic heuristic", e);
            return heuristicScore(resumeText, jdText);
        }
    }

    private ResumeMatchAnalysisResult callOllama(String resumeText, String jdText) throws JsonProcessingException {
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
                        buildPrompt(resumeText, jdText),
                        false,
                        "json"
                ))
                .retrieve()
                .body(OllamaGenerateResponse.class);

        if (response == null || response.response() == null || response.response().isBlank()) {
            throw new IllegalStateException("Ollama returned an empty response");
        }
        return parseJsonResult(response.response());
    }

    private String buildPrompt(String resumeText, String jdText) {
        return """
                You are a senior backend engineering recruiter. Compare the resume against the job description.
                Return JSON only with this exact schema:
                {
                  "score": 0-100,
                  "summary": "one concise paragraph",
                  "strengths": ["matched evidence from resume"],
                  "gaps": ["important missing skill or weak evidence"],
                  "recommendations": ["specific action to improve match"]
                }

                Scoring rubric:
                - Backend Java/Spring evidence, distributed systems, databases, cloud, messaging, and API ownership matter most.
                - Penalize generic claims without concrete project evidence.
                - Be practical for the US backend Java job market.

                Job description:
                %s

                Resume:
                %s
                """.formatted(truncate(jdText, MAX_JD_CHARS), truncate(resumeText, MAX_RESUME_CHARS));
    }

    private ResumeMatchAnalysisResult parseJsonResult(String rawResponse) throws JsonProcessingException {
        String json = rawResponse.trim();
        int start = json.indexOf('{');
        int end = json.lastIndexOf('}');
        if (start >= 0 && end > start) {
            json = json.substring(start, end + 1);
        }
        return objectMapper.readValue(json, ResumeMatchAnalysisResult.class);
    }

    private ResumeMatchAnalysisResult heuristicScore(String resumeText, String jdText) {
        Set<String> jdTerms = extractTerms(jdText);
        Set<String> resumeTerms = extractTerms(resumeText);

        List<String> matched = jdTerms.stream()
                .filter(resumeTerms::contains)
                .sorted()
                .toList();
        List<String> missing = jdTerms.stream()
                .filter(term -> !resumeTerms.contains(term))
                .sorted(Comparator.comparingInt(String::length).reversed())
                .limit(8)
                .toList();

        int score = jdTerms.isEmpty() ? 50 : Math.min(95, Math.max(25, (int) Math.round(100.0 * matched.size() / jdTerms.size())));
        ResumeMatchAnalysisResult result = new ResumeMatchAnalysisResult();
        result.setScore(score);
        result.setSummary("Deterministic fallback score based on overlap between JD keywords and extracted resume text. Connect Ollama for richer evidence-based scoring.");
        result.setStrengths(matched.stream().limit(8).map(term -> "Resume shows evidence of " + term).toList());
        result.setGaps(missing.stream().map(term -> "JD asks for " + term + ", but the resume evidence is weak or missing").toList());
        result.setRecommendations(List.of(
                "Add project bullets that quantify Java/Spring Boot ownership and production impact.",
                "Mention cloud, messaging, caching, observability, and deployment details where they are real.",
                "Tailor the resume keywords to the JD before applying."
        ));
        normalize(result);
        return result;
    }

    private Set<String> extractTerms(String text) {
        Matcher matcher = TOKEN_PATTERN.matcher(text == null ? "" : text);
        Set<String> terms = new LinkedHashSet<>();
        while (matcher.find()) {
            String token = matcher.group().toLowerCase(Locale.ROOT);
            if (STOP_WORDS.contains(token) || token.length() < 3) {
                continue;
            }
            terms.add(token);
        }
        return terms;
    }

    private void normalize(ResumeMatchAnalysisResult result) {
        result.setScore(Math.max(0, Math.min(100, result.getScore())));
        result.setSummary(result.getSummary() == null ? "" : result.getSummary());
        result.setStrengths(cleanList(result.getStrengths()));
        result.setGaps(cleanList(result.getGaps()));
        result.setRecommendations(cleanList(result.getRecommendations()));
    }

    private List<String> cleanList(List<String> values) {
        if (values == null) {
            return new ArrayList<>();
        }
        return values.stream()
                .filter(value -> value != null && !value.isBlank())
                .limit(10)
                .toList();
    }

    private String truncate(String value, int limit) {
        if (value == null || value.length() <= limit) {
            return value;
        }
        return value.substring(0, limit);
    }

    private record OllamaGenerateRequest(String model, String prompt, boolean stream, String format) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaGenerateResponse(String response) {
    }
}
