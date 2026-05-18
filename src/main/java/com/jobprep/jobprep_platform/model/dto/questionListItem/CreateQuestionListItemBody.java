package com.jobprep.jobprep_platform.model.dto.questionListItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateQuestionListItemBody {
    @NotNull(message = "question list id cannnot be null")
    @Min(value = 1, message = "question list id is a positive integer")
    private Integer questionListId;

    @NotNull(message = "question id cannot be null")
    @Min(value = 1, message = "question id is a positive integer")
    private Integer questionId;
    

}
