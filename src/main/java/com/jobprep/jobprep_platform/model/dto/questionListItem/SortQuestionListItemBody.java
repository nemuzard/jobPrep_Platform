package com.jobprep.jobprep_platform.model.dto.questionListItem;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;
@Data
public class SortQuestionListItemBody {
    @NotNull(message = "question list id cannot be null")
    @Min(value = 1, message = "question list id is a positive integer")
    private Integer questionListId;
    @NotNull(message = "question list item ids cannot be null")
    private List<Integer> questionIds;
}
