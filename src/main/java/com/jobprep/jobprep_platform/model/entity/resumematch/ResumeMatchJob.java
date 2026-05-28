package com.jobprep.jobprep_platform.model.entity.resumematch;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class ResumeMatchJob {
    private Long jobId;
    private Long userId;
    private String objectKey;
    private String originalFileName;
    private String contentType;
    private String jdText;
    private String status;
    private Integer progress;
    private Integer score;
    private String summary;
    private String strengthsJson;
    private String gapsJson;
    private String recommendationsJson;
    private String errorMessage;
    private String resumeTextHash;
    private String jdHash;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
