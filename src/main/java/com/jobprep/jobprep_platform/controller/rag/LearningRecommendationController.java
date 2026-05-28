package com.jobprep.jobprep_platform.controller.rag;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.rag.JdLearningRecommendationRequest;
import com.jobprep.jobprep_platform.model.dto.rag.RebuildLearningResourceIndexRequest;
import com.jobprep.jobprep_platform.model.vo.rag.JdLearningRecommendationVO;
import com.jobprep.jobprep_platform.model.vo.rag.RebuildLearningResourceIndexVO;
import com.jobprep.jobprep_platform.service.rag.JdLearningRecommendationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/rag/learning-recommendations")
@RequiredArgsConstructor
public class LearningRecommendationController {
    private final JdLearningRecommendationService recommendationService;

    @PostMapping("/index/rebuild")
    public ApiResponse<RebuildLearningResourceIndexVO> rebuildIndex(
            @Valid @RequestBody RebuildLearningResourceIndexRequest request
    ) {
        return recommendationService.rebuildIndex(request);
    }

    @PostMapping
    public ApiResponse<JdLearningRecommendationVO> recommend(
            @Valid @RequestBody JdLearningRecommendationRequest request
    ) {
        return recommendationService.recommend(request);
    }
}
