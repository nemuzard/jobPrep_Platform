package com.jobprep.jobprep_platform.model.dto.category;

import org.hibernate.validator.constraints.Length;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UpdateCategoryBody {
    @NotBlank(message = "name cannot be blank")
    @NotNull(message = "name cannot be blank")
    @Length(max = 20, message = "name cannot be longer than 20 characters")
    private String name;
    
}
