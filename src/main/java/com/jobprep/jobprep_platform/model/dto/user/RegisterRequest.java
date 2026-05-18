package com.jobprep.jobprep_platform.model.dto.user;

import lombok.Data;
import jakarta.validation.constraints.*;

/**
 * user register request DTO
 */
@Data
public class RegisterRequest {
    /**
     * account, required, 6-32, letters/nums/_
     */
    @NotBlank(message = "Account cannot be empty")
    @Size(min=6,max=32,message = "length must between 6-32 characters.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "only numbers, letters, and underscore are allowed")
    private String account;

    /**
     * username
     * required, Length 1-16 characters, supports Chinese characters, letters, numbers, underscores, and separators.
     */
    @NotBlank(message = "username is required")
    @Size(max=16,message = "No longer than 16 characters")
    @Pattern(regexp="^[\\u4e00-\\u9fa5_a-zA-Z0-9\\-\\.]+$",message = "Usernames can only contain Chinese characters, letters, numbers, underscores, and separators." )
    private String username;

    /**
     * password
     * required, 6-32
     */
    @NotBlank(message = "Password is required")
    @Size(min=6,max=32,message="length must between 6-32 characters")
    private String password;

    /**
     * email(optional)
     */
    @Email(message="Wrong email format")
    private String email;

    /**
     * verification code
     */
    @Size(min=6,max=6,message = "The verification code must be 6 digits long.")
    private String verifyCode;
}
