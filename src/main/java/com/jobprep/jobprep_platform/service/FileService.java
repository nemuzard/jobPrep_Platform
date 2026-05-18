package com.jobprep.jobprep_platform.service;


import org.springframework.web.multipart.MultipartFile;

public interface FileService {

    /**
     * upload file, return access address
     * @param file
     * @return
     */
    String uploadFile(MultipartFile file);

    /**
     * upload images, return access address
     * @param file
     * @return
     */
    String uploadImage(MultipartFile file);
    
} 