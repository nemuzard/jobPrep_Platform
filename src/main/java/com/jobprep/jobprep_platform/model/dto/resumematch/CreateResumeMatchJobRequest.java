package com.jobprep.jobprep_platform.model.dto.resumematch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class CreateResumeMatchJobRequest {
    @NotBlank
    @Size(max = 255)
    private String fileName;

    @Size(max = 128)
    private String contentType;

    @NotBlank
    @Size(max = 20000)
    private String jdText;
}
