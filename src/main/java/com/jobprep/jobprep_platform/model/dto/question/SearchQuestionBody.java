package com.jobprep.jobprep_platform.model.dto.question;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

@Data
public class SearchQuestionBody {
    @NotNull(message = "keyword cannot be empty")
    @NotEmpty(message = "keyword cannot be empty")
    @Length(min = 1, max = 32, message = "keyword length must within 32 characters")
    private String keyword;

    // pagination
    @Min(value = 1, message = "page must be at least 1")
    private Integer page = 1;

    @Min(value = 1, message = "pageSize must be at least 1")
    @Max(value = 100, message = "pageSize must not exceed 100")
    private Integer pageSize = 10;
}
