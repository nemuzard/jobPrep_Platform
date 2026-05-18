package com.jobprep.jobprep_platform.model.dto.user;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UploadImageResponse {
    private Integer code;
    private String msg;
    private UploadImageResponseData data;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UploadImageResponseData{
        private String url;
    }
    
}
