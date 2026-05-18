package com.jobprep.jobprep_platform.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateCommentRequest {
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
