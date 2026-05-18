package com.jobprep.jobprep_platform.model.dto.question;

import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateQuestionBody {
    @NotNull(message = "category id cannot be null")
    @Min(value = 1, message = "category must greater than 0 ")
    private Integer categoryId;

    @NotNull(message = "title cannt be empty")
    @NotBlank(message = "title cannt be empty")
    @Length(message = "cannot exceed 255 in length")
    private String title;

    @NotNull(message = "difficulty cannt be empty")
    @Range(min = 1, max = 3, message = "difficulty: 1 - easy, 2 - med, 3 - hard")
    private Integer difficulty;

    @Length(max = 255,message = "cannot exceed 255 characters")
    private String examPoint;
}
