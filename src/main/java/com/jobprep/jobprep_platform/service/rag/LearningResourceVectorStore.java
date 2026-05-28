package com.jobprep.jobprep_platform.service.rag;

import com.jobprep.jobprep_platform.model.entity.rag.LearningResourceDocument;
import com.jobprep.jobprep_platform.model.entity.rag.LearningResourceHit;

import java.util.List;

public interface LearningResourceVectorStore {
    void upsert(LearningResourceDocument document, double[] embedding);

    List<LearningResourceHit> search(double[] queryEmbedding, int topK);
}
