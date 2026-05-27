package com.jobprep.jobprep_platform.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.vo.upload.ImageVO;
import com.jobprep.jobprep_platform.service.UploadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class UploadController {
    private final UploadService uploadService;

    @PostMapping("/image")
    public ApiResponse<ImageVO> uploadImage(@RequestParam("file") MultipartFile file) {
        return uploadService.uploadImage(file);
    }
}
