package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.enums.redisKey.RedisKey;
import com.jobprep.jobprep_platform.service.EmailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import com.jobprep.jobprep_platform.utils.RandomCodeUtil;
import com.jobprep.jobprep_platform.task.email.EmailTask;


@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${mail.verify-code.limit-expire-seconds}")
    private int limitExpireSeconds;
    @Override
    public String sendVerificationCode(String email) {
        // check frequency
        if (isVerificationCodeRateLimited(email)){
            throw new RuntimeException("Verification code request is too frequent. Please try again later.");
        }
        // generate verification code
        String verificationCode = RandomCodeUtil.generateNumberCode(6);
        // send email 
        try{
            //create email task 
            EmailTask emailTask = new EmailTask();
            emailTask.setEmail(email);
            emailTask.setCode(verificationCode);
            emailTask.setTimestamp(System.currentTimeMillis());
            // push to redis queue
            String emailTaskJson = objectMapper.writeValueAsString(emailTask);
            String queueKey = RedisKey.emailTaskQueue();
            redisTemplate.opsForList().leftPush(queueKey, emailTaskJson);
            // set rate limit key
            String emailLimitkey = RedisKey.registerVerificationLimitCode(email);
            redisTemplate.opsForValue().set(emailLimitkey, "1", limitExpireSeconds,TimeUnit.SECONDS);

            return verificationCode;
        } catch (Exception e){
            log.error("Failed to send verification code email to {}: {}", email, e.getMessage());
            throw new RuntimeException("Failed to send verification code email. Please try again later.");
        }
    }

    @Override
    public boolean checkVerificationCode(String email, String code) {
        String redisKey = RedisKey.registerVerificationCode(email);
        String verificationCode = redisTemplate.opsForValue().get(redisKey);
        if (verificationCode != null && verificationCode.equals(code)){
            // delete the code after successful verification
            redisTemplate.delete(redisKey);
            return true;
        }
        return false;
    }
    @Override
    public boolean isVerificationCodeRateLimited(String email) {

        String redisKey = RedisKey.registerVerificationLimitCode(email);
        return redisTemplate.opsForValue().get(redisKey) != null;
    }
}
