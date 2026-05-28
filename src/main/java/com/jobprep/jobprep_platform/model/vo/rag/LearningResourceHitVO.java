package com.jobprep.jobprep_platform.model.vo.rag;

import lombok.Data;

@Data
public class LearningResourceHitVO {
    private String resourceType;
    private Long resourceId;
    private String title;
    private String snippet;
    private Double similarity;
}
