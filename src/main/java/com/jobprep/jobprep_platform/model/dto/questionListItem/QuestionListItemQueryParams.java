package com.jobprep.jobprep_platform.model.dto.questionListItem;

import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class QuestionListItemQueryParams {
    @NotNull(message = "question list id cannot be null")
    @Min(value = 1, message = "question list id is a positive integer")
    private Integer questionListId;
    @NotNull(message = "page cannot be null")
    @Min(value = 1, message = "page number is a positive integer")
    private Integer page;
    @NotNull(message = "page size cannot be null")
    @Range(min = 1, max = 100, message = "page size: from 1 to 100")
    private Integer pageSize;
}
