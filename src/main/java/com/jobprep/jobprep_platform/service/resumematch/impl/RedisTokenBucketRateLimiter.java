package com.jobprep.jobprep_platform.service.resumematch.impl;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class RedisTokenBucketRateLimiter {
    private static final String TOKEN_BUCKET_SCRIPT = """
            local tokens_key = KEYS[1]
            local timestamp_key = KEYS[2]
            local capacity = tonumber(ARGV[1])
            local refill_tokens = tonumber(ARGV[2])
            local refill_seconds = tonumber(ARGV[3])
            local now = tonumber(ARGV[4])
            local requested = tonumber(ARGV[5])

            local last_tokens = tonumber(redis.call('get', tokens_key))
            if last_tokens == nil then
                last_tokens = capacity
            end

            local last_refreshed = tonumber(redis.call('get', timestamp_key))
            if last_refreshed == nil then
                last_refreshed = now
            end

            local delta = math.max(0, now - last_refreshed)
            local filled_tokens = math.min(capacity, last_tokens + (delta / refill_seconds) * refill_tokens)
            local allowed = filled_tokens >= requested
            local new_tokens = filled_tokens
            if allowed then
                new_tokens = filled_tokens - requested
            end

            local ttl = math.max(refill_seconds * 2, 60)
            redis.call('setex', tokens_key, ttl, tostring(new_tokens))
            redis.call('setex', timestamp_key, ttl, tostring(now))

            if allowed then
                return 1
            end
            return 0
            """;

    private final StringRedisTemplate redisTemplate;
    private final DefaultRedisScript<Long> script;

    public RedisTokenBucketRateLimiter(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.script = new DefaultRedisScript<>(TOKEN_BUCKET_SCRIPT, Long.class);
    }

    public boolean tryAcquire(String key, int capacity, int refillTokens, int refillSeconds) {
        Long allowed = redisTemplate.execute(
                script,
                List.of(key + ":tokens", key + ":timestamp"),
                String.valueOf(capacity),
                String.valueOf(refillTokens),
                String.valueOf(refillSeconds),
                String.valueOf(Instant.now().getEpochSecond()),
                "1"
        );
        return Long.valueOf(1L).equals(allowed);
    }
}
