package com.jobprep.jobprep_platform.model.dto.collection;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CollectionQueryParams {
    @NotNull(message = "creator id cannot be blank ")
    @Min(value = 1, message = "creator id must be greater than 0")
    private Long creatorId;

    @Min(value = 1, message = "note id must be greater than 0")
    private Integer noteId;
}
