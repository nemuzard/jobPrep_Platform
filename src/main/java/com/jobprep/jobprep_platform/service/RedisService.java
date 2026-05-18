package com.jobprep.jobprep_platform.service;

// custom abstraction layer over Redis operations.
// Avoid exposing RedisTemplate directly to business logic.
public interface RedisService {

    // save data to redis
    void set(String key, Object value);

    // save and set expire time 
    void setWithExpiry(String key, Object value, long timeout );

    // get value from redis, if not exist -> null
    Object get(String key);

    // delete from redis
    void delete(String key);

    // if exist 
    boolean exists(String key);


    // counters
    Long increment(String key, long delta);
    
    Object  getHashValue(String hashKey, String key);
    void setHashValue(String hashKey, String key, Object value);
    
}
