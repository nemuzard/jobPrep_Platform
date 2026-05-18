package com.jobprep.jobprep_platform.model.dto.comment;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CommentQueryParams {
    @NotNull(message = "Note ID cannot be null")
    private Integer noteId;

    @NotNull(message = "Page number cannot be null")
    @Min(value = 1, message = "Page number must be at least 1")
    private Integer page;
    @NotNull(message = "Page size cannot be null")
    @Min(value = 1, message = "Page size must be at least 1")
    private Integer pageSize;
}

