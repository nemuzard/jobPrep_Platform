package com.jobprep.jobprep_platform.model.base;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * @param <T> Response data type
 */
@Data
@NoArgsConstructor
public class ApiResponse<T> {
    // status code
    private int code;

    // response message
    private String message;

    // data
    private T data;

    /**
     * Construct
     * @param code
     * @param message
     * @param data
     */
    public ApiResponse(int code, String message, T data){
        this.code=code;
        this.message=message;
        this.data=data;
    }

    /**
     *  create success response
     * @param <T>
     * @param data
     * @return
     */
    public static<T> ApiResponse<T> success(T data){
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(200);
        response.setMessage("success");
        response.setData(data);
        return response;
    }
    /**
     * no data - success response
     * @return 
     */
    public static ApiResponse<EmptyVO> success(){
        return success(new EmptyVO());
    }

    /**
     * false response
     * @param <T>
     * @param code - false
     * @param message
     * @return
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        return response;
    }
    /**
     * 
     * @param <T>
     * @param code
     * @param message
     * @param data
     * @return
     */
    public static <T> ApiResponse<T> error(int code, String message, T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.setCode(code);
        response.setMessage(message);
        response.setData(data);
        return response;
    }



}
