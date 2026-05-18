package com.jobprep.jobprep_platform.service.serviceImpl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.jobprep.jobprep_platform.service.UploadService;
import org.springframework.web.multipart.MultipartFile;
import com.jobprep.jobprep_platform.service.FileService;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.vo.upload.ImageVO;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;

@Service
public class UploadServiceImpl implements UploadService {
    @Autowired
    FileService fileService;

    @Override
    public ApiResponse<ImageVO> uploadImage(MultipartFile file){
        String url = fileService.uploadImage(file);
        ImageVO imageVO = new ImageVO();
        imageVO.setUrl(url);
        return ApiResponseUtil.success("Upload success!",imageVO);
    }
}
