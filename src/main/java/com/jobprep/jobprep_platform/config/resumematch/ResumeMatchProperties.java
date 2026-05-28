package com.jobprep.jobprep_platform.config.resumematch;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.resume-match")
public class ResumeMatchProperties {
    private Queue queue = new Queue();
    private Storage storage = new Storage();
    private RateLimit rateLimit = new RateLimit();
    private Cache cache = new Cache();

    @Data
    public static class Queue {
        private String exchange = "resume.match.exchange";
        private String routingKey = "resume.match.parse-score";
        private String queueName = "resume.match.parse-score.queue";
    }

    @Data
    public static class Storage {
        private String provider = "local";
        private String localRoot = "tmp/resume-objects";
        private long uploadUrlTtlSeconds = 900;
    }

    @Data
    public static class RateLimit {
        private int capacity = 5;
        private int refillTokens = 5;
        private int refillSeconds = 60;
    }

    @Data
    public static class Cache {
        private long resultTtlMinutes = 720;
    }
}
