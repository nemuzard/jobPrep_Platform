package com.jobprep.jobprep_platform.service.rag;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.rag.JdLearningRecommendationRequest;
import com.jobprep.jobprep_platform.model.dto.rag.RebuildLearningResourceIndexRequest;
import com.jobprep.jobprep_platform.model.vo.rag.JdLearningRecommendationVO;
import com.jobprep.jobprep_platform.model.vo.rag.RebuildLearningResourceIndexVO;

public interface JdLearningRecommendationService {
    ApiResponse<RebuildLearningResourceIndexVO> rebuildIndex(RebuildLearningResourceIndexRequest request);

    ApiResponse<JdLearningRecommendationVO> recommend(JdLearningRecommendationRequest request);
}
