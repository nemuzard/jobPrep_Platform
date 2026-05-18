package com.jobprep.jobprep_platform.model.enums.redisKey;

public class RedisKey {
    // generate redis key name 

    // register verification code 
    public static String registerVerificationCode(String email){
        return "email:register_verification_code:"+email;
    }

    // verification code limit
    public static String registerVerificationLimitCode(String email){
        return "email:register_verification_code:limit:"+email;

    }

    // email task 
    public static String emailTaskQueue(){
        return "queue:email:task";
    }
}
