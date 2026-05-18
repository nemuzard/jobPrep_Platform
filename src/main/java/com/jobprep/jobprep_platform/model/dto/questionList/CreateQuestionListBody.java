package com.jobprep.jobprep_platform.model.dto.questionList;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import lombok.Data;

@Data
public class CreateQuestionListBody {
    @Length(max = 32, message = "name length cannot greater than 32")
    private String name;

    @Range(min = 1, max = 2, message = "type must be 1 or 2")
    private Integer type;

    @Length(max = 255, message = "description cannot longer than 255")
    private String description;
}
