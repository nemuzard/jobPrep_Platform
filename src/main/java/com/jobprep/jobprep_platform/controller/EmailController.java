package com.jobprep.jobprep_platform.controller;

import com.jobprep.jobprep_platform.model.base.ApiResponse;
import com.jobprep.jobprep_platform.service.EmailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import com.jobprep.jobprep_platform.utils.ApiResponseUtil;

@RestController
@RequestMapping("/api/email")
public class EmailController {
    @Autowired
    private EmailService emailService;
    @GetMapping("/verify-code")
    public ApiResponse<Void> sendVerifyCode(@RequestParam @NotBlank @Email String email){
        try{
            emailService.sendVerificationCode(email);
            return ApiResponseUtil.success(null);

        }catch (Exception e){
            return ApiResponseUtil.error("Failed to send verification code: " + e.getMessage());
        }

    }
   
}
