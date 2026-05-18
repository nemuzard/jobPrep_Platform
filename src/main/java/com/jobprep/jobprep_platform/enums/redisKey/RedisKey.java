package com.jobprep.jobprep_platform.enums.redisKey;

public class RedisKey {
    
    /**
     * 
     * @param email - user email
     * @return format: email:register_verification_code:{email} - redis key
     */
    public static String registerVerificationCode(String email){
        return "email:register_verification_code"+email;
    }
    /**
     *  redis key for rate limiting of verification code request
     *  used to track frequency limits on users sending verification codes
     * @param email
     * @return
     */
    public static String registerVerificationLimitCode(String email){
        return "email:register_verification_limit_code"+email;
    }

    public static String emailTaskQueue(){
        return "queue:email:task";
    }
}
