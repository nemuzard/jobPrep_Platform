package com.jobprep.jobprep_platform.model.vo.rag;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RebuildLearningResourceIndexVO {
    private int indexedNotes;
    private int indexedQuestions;
    private int totalIndexed;
}
