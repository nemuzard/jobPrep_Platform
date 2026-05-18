package com.jobprep.jobprep_platform.model.base;

import lombok.Data;
import lombok.EqualsAndHashCode;
@Data
@EqualsAndHashCode(callSuper=true)
public class TokenApiResponse<T> extends ApiResponse {

    private final String token;

    public TokenApiResponse(Integer code, String msg, T data, String token){
        super(code,msg,data);
        this.token = token;
    }
    
}
