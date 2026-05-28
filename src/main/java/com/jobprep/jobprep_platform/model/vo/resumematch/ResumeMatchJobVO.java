package com.jobprep.jobprep_platform.model.vo.resumematch;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Data;

@Data
public class ResumeMatchJobVO {
    private Long jobId;
    private String originalFileName;
    private String contentType;
    private String status;
    private Integer progress;
    private Integer score;
    private String summary;
    private List<String> strengths;
    private List<String> gaps;
    private List<String> recommendations;
    private String errorMessage;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
}
