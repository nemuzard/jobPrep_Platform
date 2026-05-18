package com.jobprep.jobprep_platform.model.dto.comment;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import lombok.Data;

@Data
public class CreateCommentRequest {
    @NotNull(message = "Note ID cannot be null")
    private Integer noteId;

    private Integer parentId;
    @NotBlank(message = "Content cannot be blank")
    private String content;
}
