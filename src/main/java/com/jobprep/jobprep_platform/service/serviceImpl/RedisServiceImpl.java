package com.jobprep.jobprep_platform.service.serviceImpl;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.jobprep.jobprep_platform.service.RedisService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String,Object> redisTemplate;
    // save data
    public void set(String key, Object value){
        redisTemplate.opsForValue().set(key, value);
    }
    //set expiration 
    public void setWithExpiry(String key,Object value, long timeout){
        redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
    }

    // get data
    public Object get(String key){
        return redisTemplate.opsForValue().get(key);
    }
    // delete
    public void delete(String key){
        redisTemplate.delete(key);
    }
    // if exist
    public boolean exists(String key){
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // increment 
    public Long increment(String key, long delta){
        return redisTemplate.opsForValue().increment(key,delta);
    }
    // get hashvalue 
    public Object getHashValue(String hashKey, String key){
        return redisTemplate.opsForHash().get(hashKey, key);
    }
    // set hashvalue
    public void setHashValue(String hashKey, String key, Object value){
        redisTemplate.opsForHash().put(hashKey,key,value);
    }

}
