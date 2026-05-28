package com.jobprep.jobprep_platform.mapper.resumematch;

import com.jobprep.jobprep_platform.model.entity.resumematch.ResumeMatchJob;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ResumeMatchJobMapper {
    int insert(ResumeMatchJob job);

    ResumeMatchJob findById(@Param("jobId") Long jobId);

    ResumeMatchJob findByIdAndUserId(@Param("jobId") Long jobId, @Param("userId") Long userId);

    int updateObjectKey(@Param("jobId") Long jobId, @Param("objectKey") String objectKey);

    int markUploaded(@Param("jobId") Long jobId);

    int updateStatusProgress(
            @Param("jobId") Long jobId,
            @Param("status") String status,
            @Param("progress") int progress,
            @Param("errorMessage") String errorMessage
    );

    int updateResult(ResumeMatchJob job);

    int updateFailure(@Param("jobId") Long jobId, @Param("errorMessage") String errorMessage);
}
