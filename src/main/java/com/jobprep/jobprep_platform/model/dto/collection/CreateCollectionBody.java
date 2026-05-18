package com.jobprep.jobprep_platform.model.dto.collection;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateCollectionBody {
    @NotNull(message = "name cannot be blank ")
    @NotBlank(message = "name cannot be blank ")
    private String name;
    private String description;
}
