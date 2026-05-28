package com.jobprep.jobprep_platform.service.resumematch;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;

public interface ResumeObjectStorageService {
    PresignedUpload createPresignedUpload(Long jobId, Long userId, String fileName, String contentType);

    void putObject(String objectKey, byte[] content) throws IOException;

    InputStream getObject(String objectKey) throws IOException;

    record PresignedUpload(String uploadUrl, String objectKey, String token, LocalDateTime expiresAt) {
    }
}
