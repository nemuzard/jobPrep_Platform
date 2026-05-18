package com.jobprep.jobprep_platform.model.dto.category;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

@Data
public class CreateCategoryBody {
    
    @NotBlank(message = "category name cannot be blank")
    @NotNull(message = "category name cannot be blank")
    @Length(max = 20, message = "category name cannot be longer than 20 characters")
    private String name;

    @NotNull(message = "parent category id cannot be null")
    @Min(value = 0, message = "parent category id must be greater than or equal to 0"   )
    private Integer parentCategoryId;
}
