package com.jobprep.jobprep_platform.model.dto.question;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class QuestionQueryParam {
    
    @Min(value = 1, message ="category id invalid")
    private Integer categoryId;

    @Pattern(regexp = "^(view|difficulty)$", message = "sort: must be view or difficulty")
    private String sort;

    @Pattern(regexp = "^(asc|desc)$", message = "order: asc or desc")
    private String order;
    
    @NotNull(message = "page invalid")
    @Min(value = 1, message = "page number is a positive integer")
    private Integer page;

    @NotNull(message = "pageSize cannot be null")
    @Min(value = 1, message = "pageSize is a positive integer")
    @Max(value = 100, message = "pageSize cannot exceed 100")
    private Integer pageSize;


}
