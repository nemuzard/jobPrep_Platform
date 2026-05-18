package com.jobprep.jobprep_platform.utils;
import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.model.base.Pagination;
import com.jobprep.jobprep_platform.model.base.PaginationApiResponse;
import com.jobprep.jobprep_platform.model.base.TokenApiResponse;
import org.springframework.http.HttpStatus;

public class ApiResponseUtil {
    /**
     * construct success response
     * @param <T>
     * @param message
     * @return api response
     */
    public static <T> ApiResponse<T> success(String message){
        return ApiResponse.success(null);
    }

    public static <T> ApiResponse<T> success(String message,T data){
        return ApiResponse.success(data);
    }

    /**
     * contruct failure response
     * @param <T>
     * @param msg
     * @return
     */
    public static <T> ApiResponse<T> error(String msg){
        return ApiResponse.error(HttpStatus.BAD_REQUEST.value(),msg);
    }

    /**
     * construct TokenApiResponse
     * @param <T>
     * @param msg
     * @param data
     * @param token
     * @return
     */
    public static <T> TokenApiResponse<T> success(String msg,T data, String token){
        return new TokenApiResponse<>(HttpStatus.OK.value(),msg,data,token);
    }

    /**
     * construct PaginationApiResponse
     * @param <T>
     * @param msg
     * @param data
     * @param pagination
     * @return
     */
    public static <T> PaginationApiResponse<T> success(String msg, T data, Pagination pagination){
        return new PaginationApiResponse<T>(HttpStatus.OK.value(), msg, data, pagination);
    }


}
