package com.jobprep.jobprep_platform.config.rag;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.rag")
public class RagProperties {
    private Pgvector pgvector = new Pgvector();
    private Retrieval retrieval = new Retrieval();

    @Data
    public static class Pgvector {
        private String url = "jdbc:postgresql://localhost:5433/jobprep_rag";
        private String username = "jobprep";
        private String password = "jobprep123";
        private String tableName = "learning_resource_embedding";
        private int embeddingDimension = 768;
    }

    @Data
    public static class Retrieval {
        private int topK = 6;
        private long cacheTtlMinutes = 720;
    }
}
