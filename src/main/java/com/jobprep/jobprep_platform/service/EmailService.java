package com.jobprep.jobprep_platform.service;

public interface EmailService {
    /**
     * 
     * @param email - target email
     * @return verification code
     */
    String sendVerificationCode(String email);

    /**
     * verify if the code is correct
     * @param email
     * @param code - user entered
     * @return T/F
     */
    boolean checkVerificationCode(String email, String code);

    /**
     * check if current email allowed to receive verification code (not exceed limit)
     * @param email
     * @return True -> cannot receive a code right now
     */
    boolean isVerificationCodeRateLimited(String email);
}
