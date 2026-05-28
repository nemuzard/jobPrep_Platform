package com.jobprep.jobprep_platform.service.resumematch.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobprep.jobprep_platform.annotation.NeedLogin;
import com.jobprep.jobprep_platform.config.resumematch.ResumeMatchProperties;
import com.jobprep.jobprep_platform.mapper.resumematch.ResumeMatchJobMapper;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.EmptyVO;
import com.jobprep.jobprep_platform.model.dto.resumematch.CreateResumeMatchJobRequest;
import com.jobprep.jobprep_platform.model.entity.resumematch.ResumeMatchJob;
import com.jobprep.jobprep_platform.model.enums.resumematch.ResumeMatchStatus;
import com.jobprep.jobprep_platform.model.message.resumematch.ResumeMatchTaskMessage;
import com.jobprep.jobprep_platform.model.vo.resumematch.CreateResumeMatchJobVO;
import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchJobVO;
import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchProgressVO;
import com.jobprep.jobprep_platform.scope.RequestScopeData;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchEventService;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchQueuePublisher;
import com.jobprep.jobprep_platform.service.resumematch.ResumeMatchService;
import com.jobprep.jobprep_platform.service.resumematch.ResumeObjectStorageService;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;
import com.jobprep.jobprep_platform.utils.resumematch.HashUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Log4j2
@Service
@RequiredArgsConstructor
public class ResumeMatchServiceImpl implements ResumeMatchService {
    private static final String UPLOAD_TOKEN_KEY = "resume-match:upload-token:%d:%s";
    private static final String RATE_LIMIT_KEY = "resume-match:rate-limit:user:%d";
    private static final TypeReference<List<String>> STRING_LIST_TYPE = new TypeReference<>() {
    };

    private final ResumeMatchJobMapper resumeMatchJobMapper;
    private final ResumeObjectStorageService objectStorageService;
    private final ResumeMatchQueuePublisher queuePublisher;
    private final ResumeMatchEventService eventService;
    private final RedisTokenBucketRateLimiter rateLimiter;
    private final StringRedisTemplate stringRedisTemplate;
    private final ResumeMatchProperties properties;
    private final RequestScopeData requestScopeData;
    private final ObjectMapper objectMapper;

    @Override
    @NeedLogin
    @Transactional
    public ApiResponse<CreateResumeMatchJobVO> createUploadJob(CreateResumeMatchJobRequest request) {
        Long userId = requestScopeData.getUserId();
        if (!rateLimiter.tryAcquire(
                RATE_LIMIT_KEY.formatted(userId),
                properties.getRateLimit().getCapacity(),
                properties.getRateLimit().getRefillTokens(),
                properties.getRateLimit().getRefillSeconds()
        )) {
            return ApiResponse.error(HttpStatus.TOO_MANY_REQUESTS.value(), "Too many resume match requests. Please retry later.");
        }

        ResumeMatchJob job = new ResumeMatchJob();
        job.setUserId(userId);
        job.setObjectKey("pending");
        job.setOriginalFileName(request.getFileName());
        job.setContentType(request.getContentType());
        job.setJdText(request.getJdText());
        job.setStatus(ResumeMatchStatus.UPLOAD_PENDING.name());
        job.setProgress(0);
        job.setJdHash(HashUtils.sha256(request.getJdText()));
        resumeMatchJobMapper.insert(job);

        ResumeObjectStorageService.PresignedUpload presignedUpload = objectStorageService.createPresignedUpload(
                job.getJobId(),
                userId,
                request.getFileName(),
                request.getContentType()
        );
        resumeMatchJobMapper.updateObjectKey(job.getJobId(), presignedUpload.objectKey());
        stringRedisTemplate.opsForValue().set(
                uploadTokenKey(job.getJobId(), presignedUpload.token()),
                presignedUpload.objectKey(),
                properties.getStorage().getUploadUrlTtlSeconds(),
                TimeUnit.SECONDS
        );

        return ApiResponseUtil.success("success", CreateResumeMatchJobVO.builder()
                .jobId(job.getJobId())
                .uploadUrl(presignedUpload.uploadUrl())
                .objectKey(presignedUpload.objectKey())
                .status(ResumeMatchStatus.UPLOAD_PENDING.name())
                .expiresAt(presignedUpload.expiresAt())
                .build());
    }

    @Override
    @NeedLogin
    public ApiResponse<ResumeMatchJobVO> getJob(Long jobId) {
        ResumeMatchJob job = resumeMatchJobMapper.findByIdAndUserId(jobId, requestScopeData.getUserId());
        if (job == null) {
            return ApiResponseUtil.error("resume match job not found");
        }
        return ApiResponseUtil.success("success", toVO(job));
    }

    @Override
    @Transactional
    public void acceptLocalUpload(Long jobId, String token, byte[] content) {
        if (token == null || token.isBlank()) {
            throw new IllegalArgumentException("Missing upload token");
        }
        String tokenKey = uploadTokenKey(jobId, token);
        String objectKey = stringRedisTemplate.opsForValue().get(tokenKey);
        if (objectKey == null) {
            throw new IllegalArgumentException("Upload URL is invalid or expired");
        }
        ResumeMatchJob job = resumeMatchJobMapper.findById(jobId);
        if (job == null || !objectKey.equals(job.getObjectKey())) {
            throw new IllegalArgumentException("Resume match job not found");
        }
        try {
            objectStorageService.putObject(objectKey, content);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to store resume object", e);
        }
        stringRedisTemplate.delete(tokenKey);
        resumeMatchJobMapper.markUploaded(jobId);
        publishProgress(jobId, ResumeMatchStatus.UPLOADED, 15, "Resume uploaded to object storage.");
        enqueueUploadedJob(jobId);
    }

    @Override
    @Transactional
    public void enqueueUploadedJob(Long jobId) {
        ResumeMatchJob job = resumeMatchJobMapper.findById(jobId);
        if (job == null) {
            throw new IllegalArgumentException("Resume match job not found");
        }
        resumeMatchJobMapper.updateStatusProgress(jobId, ResumeMatchStatus.QUEUED.name(), 20, null);
        queuePublisher.publish(new ResumeMatchTaskMessage(job.getJobId(), job.getUserId(), job.getObjectKey()));
        publishProgress(jobId, ResumeMatchStatus.QUEUED, 20, "Resume parsing and scoring task queued.");
    }

    public ResumeMatchJobVO toVO(ResumeMatchJob job) {
        ResumeMatchJobVO vo = new ResumeMatchJobVO();
        vo.setJobId(job.getJobId());
        vo.setOriginalFileName(job.getOriginalFileName());
        vo.setContentType(job.getContentType());
        vo.setStatus(job.getStatus());
        vo.setProgress(job.getProgress());
        vo.setScore(job.getScore());
        vo.setSummary(job.getSummary());
        vo.setStrengths(parseStringList(job.getStrengthsJson()));
        vo.setGaps(parseStringList(job.getGapsJson()));
        vo.setRecommendations(parseStringList(job.getRecommendationsJson()));
        vo.setErrorMessage(job.getErrorMessage());
        vo.setCreatedAt(job.getCreatedAt());
        vo.setUpdatedAt(job.getUpdatedAt());
        vo.setCompletedAt(job.getCompletedAt());
        return vo;
    }

    private List<String> parseStringList(String value) {
        if (value == null || value.isBlank()) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(value, STRING_LIST_TYPE);
        } catch (JsonProcessingException e) {
            log.warn("failed to parse resume match JSON list", e);
            return Collections.emptyList();
        }
    }

    private void publishProgress(Long jobId, ResumeMatchStatus status, int progress, String message) {
        eventService.publish(ResumeMatchProgressVO.builder()
                .jobId(jobId)
                .status(status.name())
                .progress(progress)
                .message(message)
                .build());
    }

    private String uploadTokenKey(Long jobId, String token) {
        return UPLOAD_TOKEN_KEY.formatted(jobId, token);
    }
}
