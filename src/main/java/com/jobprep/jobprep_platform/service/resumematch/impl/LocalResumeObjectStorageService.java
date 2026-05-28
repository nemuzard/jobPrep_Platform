package com.jobprep.jobprep_platform.service.resumematch.impl;

import com.jobprep.jobprep_platform.config.resumematch.ResumeMatchProperties;
import com.jobprep.jobprep_platform.service.resumematch.ResumeObjectStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class LocalResumeObjectStorageService implements ResumeObjectStorageService {
    private final ResumeMatchProperties properties;
    private final Path root;

    public LocalResumeObjectStorageService(ResumeMatchProperties properties) throws IOException {
        this.properties = properties;
        this.root = Path.of(properties.getStorage().getLocalRoot()).toAbsolutePath().normalize();
        Files.createDirectories(root);
    }

    @Override
    public PresignedUpload createPresignedUpload(Long jobId, Long userId, String fileName, String contentType) {
        String token = UUID.randomUUID().toString();
        String safeName = sanitizeFileName(fileName);
        String objectKey = "resume-match/%d/%d/%s-%s".formatted(
                userId,
                jobId,
                UUID.randomUUID(),
                safeName
        );
        String uploadUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/resume-match/uploads/")
                .path(String.valueOf(jobId))
                .queryParam("token", token)
                .toUriString();
        LocalDateTime expiresAt = LocalDateTime.now().plusSeconds(properties.getStorage().getUploadUrlTtlSeconds());
        return new PresignedUpload(uploadUrl, objectKey, token, expiresAt);
    }

    @Override
    public void putObject(String objectKey, byte[] content) throws IOException {
        Path target = resolveObjectPath(objectKey);
        Files.createDirectories(target.getParent());
        Files.write(target, content);
    }

    @Override
    public InputStream getObject(String objectKey) throws IOException {
        return Files.newInputStream(resolveObjectPath(objectKey));
    }

    private Path resolveObjectPath(String objectKey) {
        Path target = root.resolve(objectKey).normalize();
        if (!target.startsWith(root)) {
            throw new IllegalArgumentException("Invalid object key");
        }
        return target;
    }

    private String sanitizeFileName(String fileName) {
        String cleaned = fileName == null ? "resume" : fileName.replaceAll("[^A-Za-z0-9._-]", "_");
        if (cleaned.isBlank()) {
            return "resume";
        }
        return cleaned.length() > 120 ? cleaned.substring(cleaned.length() - 120) : cleaned;
    }
}
