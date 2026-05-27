package com.jobprep.jobprep_platform.task.email;

import java.util.concurrent.TimeUnit;
import com.jobprep.jobprep_platform.enums.redisKey.RedisKey;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;

@Component
@ConditionalOnProperty(name = "app.scheduling.enabled", havingValue = "true", matchIfMissing = true)
public class EmailTaskConsumer {
    @Autowired
    private JavaMailSender mailSender;
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Value("${spring.mail.username}")
    private String from;

    @Scheduled(fixedDelay = 3000) // every 3 seconds
    public void consumeEmailTasks() {
        String emailQueueKey = RedisKey.emailTaskQueue();
        while (true){
            String emailTaskJson = redisTemplate.opsForList().rightPop(emailQueueKey);
            if(emailTaskJson == null){
                break;
            }
            // Process the email task
            try{
                EmailTask emailTask = objectMapper.readValue(emailTaskJson, EmailTask.class);
                String email = emailTask.getEmail();
                String verificationCode = emailTask.getCode();

                //according to eamil task object, send email
                // fill simplemailmessage and use javamailsender to send email
                SimpleMailMessage mailMessage = new SimpleMailMessage();
                mailMessage.setFrom(from);
                mailMessage.setTo(email);
                mailMessage.setSubject("Your Verification Code");
                mailMessage.setText("Your verification code is: " + verificationCode + ". It will expire in "+5+" minutes.");
                mailSender.send(mailMessage);
                // save to redis, with expiration time of 5 minutes
                redisTemplate.opsForValue().set(RedisKey.registerVerificationCode(email),verificationCode,5,TimeUnit.MINUTES);
            }catch(JsonProcessingException e){
                e.printStackTrace();
            }catch(Exception e){
                e.printStackTrace();
            }    
        }
    }
}
