package com.jobprep.jobprep_platform.model.dto.rag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class JdLearningRecommendationRequest {
    @NotBlank
    @Size(max = 20000)
    private String jdText;

    @Min(1)
    @Max(20)
    private Integer topK;
}
