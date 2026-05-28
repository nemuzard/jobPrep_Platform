package com.jobprep.jobprep_platform.model.vo.resumematch;

import java.time.LocalDateTime;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CreateResumeMatchJobVO {
    private Long jobId;
    private String uploadUrl;
    private String objectKey;
    private String status;
    private LocalDateTime expiresAt;
}
