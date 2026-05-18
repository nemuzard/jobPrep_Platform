package com.jobprep.jobprep_platform.model.dto.user;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.AssertTrue;

@Data
public class LoginRequest {
    /**
     * user account
     */
    @Size(min=6,max=32,message="account length must between 6-32")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message="only letters, numbers, _ are allowed.")
    private String account;

    @Email(message = "Wrong email format")
    private String email;

    /**
     * password
     */
    @NotBlank(message="password is required")
    @Size(min=6,max=32,message = "length must be in between 6 and 32")
    private String password;

    @AssertTrue(message = "Please provide either email or account number.")
    private boolean isValidLogin(){
        return account!=null || email!=null;
    }
}   
