package com.jobprep.jobprep_platform.config.ai;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.ai")
public class AiProperties {
    private String provider = "ollama";
    private Ollama ollama = new Ollama();

    @Data
    public static class Ollama {
        private String baseUrl = "http://localhost:11434";
        private String model = "llama3.1:8b";
        private String embeddingModel = "nomic-embed-text";
        private int timeoutSeconds = 120;
    }
}
