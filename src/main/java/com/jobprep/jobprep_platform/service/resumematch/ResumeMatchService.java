package com.jobprep.jobprep_platform.service.resumematch;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.dto.resumematch.CreateResumeMatchJobRequest;
import com.jobprep.jobprep_platform.model.vo.resumematch.CreateResumeMatchJobVO;
import com.jobprep.jobprep_platform.model.vo.resumematch.ResumeMatchJobVO;

public interface ResumeMatchService {
    ApiResponse<CreateResumeMatchJobVO> createUploadJob(CreateResumeMatchJobRequest request);

    ApiResponse<ResumeMatchJobVO> getJob(Long jobId);

    void acceptLocalUpload(Long jobId, String token, byte[] content);

    void enqueueUploadedJob(Long jobId);
}
