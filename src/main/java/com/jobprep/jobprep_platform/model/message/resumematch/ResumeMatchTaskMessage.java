package com.jobprep.jobprep_platform.model.message.resumematch;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumeMatchTaskMessage {
    private Long jobId;
    private Long userId;
    private String objectKey;
}
