package com.jobprep.jobprep_platform.model.dto.note;

import lombok.Data;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Data
public class CreateNoteRequest {
    @NotNull(message = "problem id cannot be null")
    @Min(value = 1, message = "problem id must be greater than 0")
    private Integer questionId;

    @NotNull(message = "content cannot be null")
    @NotBlank(message = "content cannot be blank")
    private String content;

}
