package com.jobprep.jobprep_platform.service.resumematch;

import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchAnalysisResult;

public interface ResumeMatchingClient {
    ResumeMatchAnalysisResult score(String resumeText, String jdText);
}
