package com.jobprep.jobprep_platform.task.resumematch;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobprep.jobprep_platform.config.resumematch.ResumeMatchProperties;
import com.jobprep.jobprep_platform.mapper.resumematch.ResumeMatchJobMapper;
import com.jobprep.jobprep_platform.model.entity.resumematch.ResumeMatchJob;
import com.jobprep.jobprep_platform.model.enums.resumematch.ResumeMatchStatus;
import com.jobprep.jobprep_platform.model.message.resumematch.ResumeMatchTaskMessage;
import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchAnalysisResult;
import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchProgressVO;
import com.jobprep.jobprep_platform.service.resumematch.DocumentTextExtractor;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchEventService;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchingClient;
import com.jobprep.jobprep_platform.service.resumematch.ResumeObjectStorageService;
import com.jobprep.jobprep_platform.utils.resumematch.HashUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

@Log4j2
@Component
@ConditionalOnProperty(name = "app.resume-match.worker.enabled", havingValue = "true", matchIfMissing = true)
@RequiredArgsConstructor
public class ResumeMatchWorker {
    private static final String SCORE_CACHE_KEY = "resume-match:score:%s:%s";

    private final ResumeMatchJobMapper resumeMatchJobMapper;
    private final ResumeObjectStorageService objectStorageService;
    private final DocumentTextExtractor documentTextExtractor;
    private final ResumeMatchingClient matchingClient;
    private final ResumeMatchEventService eventService;
    private final StringRedisTemplate stringRedisTemplate;
    private final ObjectMapper objectMapper;
    private final ResumeMatchProperties properties;

    @RabbitListener(queues = "${app.resume-match.queue.queue-name}")
    public void consume(ResumeMatchTaskMessage message) {
        Long jobId = message.getJobId();
        try {
            ResumeMatchJob job = resumeMatchJobMapper.findById(jobId);
            if (job == null) {
                log.warn("resume match job {} not found", jobId);
                return;
            }

            updateProgress(jobId, ResumeMatchStatus.PARSING, 35, "Extracting resume text from object storage.");
            String resumeText;
            try (InputStream inputStream = objectStorageService.getObject(job.getObjectKey())) {
                resumeText = documentTextExtractor.extract(inputStream, job.getOriginalFileName(), job.getContentType());
            }
            if (resumeText == null || resumeText.isBlank()) {
                throw new IllegalStateException("No readable text was extracted from resume");
            }

            String resumeHash = HashUtils.sha256(resumeText);
            String cacheKey = SCORE_CACHE_KEY.formatted(resumeHash, job.getJdHash());
            ResumeMatchAnalysisResult result = readCachedResult(cacheKey);

            if (result == null) {
                updateProgress(jobId, ResumeMatchStatus.SCORING, 70, "Calling LLM provider to score resume and JD match.");
                result = matchingClient.score(resumeText, job.getJdText());
                stringRedisTemplate.opsForValue().set(
                        cacheKey,
                        objectMapper.writeValueAsString(result),
                        properties.getCache().getResultTtlMinutes(),
                        TimeUnit.MINUTES
                );
            } else {
                updateProgress(jobId, ResumeMatchStatus.SCORING, 80, "Reusing cached match result for the same resume and JD.");
            }

            ResumeMatchJob update = new ResumeMatchJob();
            update.setJobId(jobId);
            update.setScore(result.getScore());
            update.setSummary(result.getSummary());
            update.setStrengthsJson(objectMapper.writeValueAsString(result.getStrengths()));
            update.setGapsJson(objectMapper.writeValueAsString(result.getGaps()));
            update.setRecommendationsJson(objectMapper.writeValueAsString(result.getRecommendations()));
            update.setResumeTextHash(resumeHash);
            resumeMatchJobMapper.updateResult(update);
            publishProgress(jobId, ResumeMatchStatus.COMPLETED, 100, "Resume match scoring completed.");
        } catch (Exception e) {
            log.error("failed to process resume match job {}", jobId, e);
            String errorMessage = e.getMessage() == null ? "resume match processing failed" : e.getMessage();
            resumeMatchJobMapper.updateFailure(jobId, errorMessage);
            publishProgress(jobId, ResumeMatchStatus.FAILED, 100, errorMessage);
        }
    }

    private ResumeMatchAnalysisResult readCachedResult(String cacheKey) {
        String cached = stringRedisTemplate.opsForValue().get(cacheKey);
        if (cached == null || cached.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(cached, ResumeMatchAnalysisResult.class);
        } catch (Exception e) {
            log.warn("failed to parse cached resume match result", e);
            stringRedisTemplate.delete(cacheKey);
            return null;
        }
    }

    private void updateProgress(Long jobId, ResumeMatchStatus status, int progress, String message) {
        resumeMatchJobMapper.updateStatusProgress(jobId, status.name(), progress, null);
        publishProgress(jobId, status, progress, message);
    }

    private void publishProgress(Long jobId, ResumeMatchStatus status, int progress, String message) {
        eventService.publish(ResumeMatchProgressVO.builder()
                .jobId(jobId)
                .status(status.name())
                .progress(progress)
                .message(message)
                .build());
    }
}
