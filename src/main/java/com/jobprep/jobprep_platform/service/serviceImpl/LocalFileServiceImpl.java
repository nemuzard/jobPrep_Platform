package com.jobprep.jobprep_platform.service.serviceImpl;

import com.jobprep.jobprep_platform.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class LocalFileServiceImpl implements FileService {
    
    @Value("${upload.path}")
    private String uploadBasePath;

    @Value("${upload.url.prefix}")
    private String urlPrefix;

    private static final List<String> ALLOWED_IMAGE_EXTENSIONS = Arrays.asList("jpg", "jpeg", "png", "webp");

    private static final long MAX_IMAGE_SIZE = 10 * 1024 * 1024L; // 10MB

    @Override
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        if (file.getSize() > MAX_IMAGE_SIZE) {
            throw new IllegalArgumentException("File size exceeds the maximum limit of 10MB");
        }
        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String lowerCaseExtension = originalFilename.substring(originalFilename.lastIndexOf(".") + 1).toLowerCase();
        if(!ALLOWED_IMAGE_EXTENSIONS.contains(lowerCaseExtension)) {
            throw new IllegalArgumentException("Unsupported file type. Allowed types are: " + ALLOWED_IMAGE_EXTENSIONS);
        }
        return doUpload(file);
    }
    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }
        return doUpload(file);
    }

    /**
     *  Handles the actual file upload process.
     *  @param file the file to be uploaded
     *  @return the URL of the uploaded file
     */
    private String doUpload(MultipartFile file) {
        String originalFilename = file.getOriginalFilename();
        if(originalFilename == null || !originalFilename.contains(".")) {
            throw new IllegalArgumentException("Invalid file name");
        }
        String fileExtension = originalFilename.substring(originalFilename.lastIndexOf(".")).toLowerCase();
        String newFilename = UUID.randomUUID()+fileExtension;
        File uploadDir = new File(uploadBasePath);
        if (!uploadDir.exists() && !uploadDir.mkdirs()) {
            throw new RuntimeException("Failed to create upload directory");
        }
        File destFile = new File(uploadDir, newFilename);
        try {
            file.transferTo(destFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file", e);
        }
        return urlPrefix + "/" + newFilename;
    }
}
