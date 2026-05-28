package com.jobprep.jobprep_platform.model.entity.rag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LearningResourceDocument {
    private String resourceType;
    private Long resourceId;
    private String title;
    private String content;
    private String metadataJson;
}
