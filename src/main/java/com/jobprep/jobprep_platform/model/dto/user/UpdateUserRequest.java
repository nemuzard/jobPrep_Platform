package com.jobprep.jobprep_platform.model.dto.user;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.Data;

/**
 * user update request DTO
 */
@Data
public class UpdateUserRequest {
    
    @Size(min=1,max=16,message="username length must in between 1 and 16")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5_a-zA-Z0-9]+$", message="Only Chinese, English letters, numbers, and _ are allowed")
    private String username;

    @Min(value=1, message="invalid")
    @Max(value=3,message="invalid")
    private Integer gender;

    @Past(message="BOB cannot be a future date")
    private LocalDate birthday;

    @Pattern(regexp = "^(https?|ftp)://.*$",message="invalid avatar url")
    private String avatarUrl;

    @Email(message="invalid email")
    private String email;

    @Size(max=64, message="School name cannot exceed 64 characters.")
    private String school;

    @Size(max=128,message="Signature cannot exceed 128 characters.")
    private String signature;
}
