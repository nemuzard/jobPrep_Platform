package com.jobprep.jobprep_platform.model.vo.rag;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class JdLearningRecommendationVO {
    private String roadmap;
    private List<String> skillKeywords = new ArrayList<>();
    private List<LearningResourceHitVO> resources = new ArrayList<>();
    private boolean cached;
}
