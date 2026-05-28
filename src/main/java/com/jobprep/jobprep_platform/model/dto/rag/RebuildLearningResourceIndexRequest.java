package com.jobprep.jobprep_platform.model.dto.rag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class RebuildLearningResourceIndexRequest {
    @Min(1)
    @Max(10000)
    private Integer noteLimit = 500;

    @Min(1)
    @Max(10000)
    private Integer questionLimit = 500;
}
