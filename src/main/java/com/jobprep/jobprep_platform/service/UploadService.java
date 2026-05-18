package com.jobprep.jobprep_platform.service;

import org.springframework.web.multipart.MultipartFile;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.vo.upload.ImageVO;
public interface  UploadService {
    ApiResponse<ImageVO> uploadImage(MultipartFile file);
}
