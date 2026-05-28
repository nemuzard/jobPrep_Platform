package com.jobprep.jobprep_platform.model.entity.rag;

import lombok.Data;

@Data
public class LearningResourceHit {
    private String resourceType;
    private Long resourceId;
    private String title;
    private String content;
    private String metadataJson;
    private double similarity;
}
