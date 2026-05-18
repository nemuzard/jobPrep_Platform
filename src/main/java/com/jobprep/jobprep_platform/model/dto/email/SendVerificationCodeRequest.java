package com.jobprep.jobprep_platform.model.dto.email;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SendVerificationCodeRequest {
    @NotBlank(message = "Email cannot be empty")
    @Email(message = "Wrong email format")
    private String email;
}

