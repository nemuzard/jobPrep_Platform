package com.jobprep.jobprep_platform.model.dto.collection;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;

/**
 * DTO for updating a collection, which includes adding or removing notes from the collection.
 */
@Data
public class UpdateCollectionBody {
    @Min(value = 1, message = "note id must be greater than 0")
    private Integer noteId;

    private UpdateItem[] items;
    @Data
    public static class UpdateItem {
        @Min(value = 1, message = "collection id must be greater than 0")

        private Integer collectionId;
        @NotNull(message = "action cannot be empty")
        @NotEmpty(message = "action cannot be empty")
        @Pattern(regexp = "create|delete", message = "action must be either 'create' or 'delete'")
        private String action;

    }

}
