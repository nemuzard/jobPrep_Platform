package com.jobprep.jobprep_platform.model.vo.resumematch;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResumeMatchProgressVO {
    private Long jobId;
    private String status;
    private Integer progress;
    private String message;
}
