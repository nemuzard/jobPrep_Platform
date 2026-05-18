package com.jobprep.jobprep_platform.model.dto.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateNoteRequest {
    @NotNull(message="note content cannot be null")
    @NotBlank(message="note content cannot be blank")
    private String content;
}
