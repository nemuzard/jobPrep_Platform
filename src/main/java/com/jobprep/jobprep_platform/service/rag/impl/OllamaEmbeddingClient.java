package com.jobprep.jobprep_platform.service.rag.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.jobprep.jobprep_platform.config.ai.AiProperties;
import com.jobprep.jobprep_platform.config.rag.RagProperties;
import com.jobprep.jobprep_platform.service.rag.EmbeddingClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.Duration;
import java.util.List;
import java.util.Random;

@Log4j2
@Service
@RequiredArgsConstructor
public class OllamaEmbeddingClient implements EmbeddingClient {
    private static final int MAX_INPUT_CHARS = 8000;

    private final AiProperties aiProperties;
    private final RagProperties ragProperties;

    @Override
    public double[] embed(String text) {
        try {
            double[] embedding = callOllama(text);
            if (embedding.length != ragProperties.getPgvector().getEmbeddingDimension()) {
                log.warn("embedding dimension {} does not match configured dimension {}; using deterministic fallback",
                        embedding.length,
                        ragProperties.getPgvector().getEmbeddingDimension());
                return deterministicEmbedding(text);
            }
            return embedding;
        } catch (Exception e) {
            log.warn("Ollama embedding failed; using deterministic fallback embedding", e);
            return deterministicEmbedding(text);
        }
    }

    private double[] callOllama(String text) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        int timeoutMs = Math.toIntExact(Duration.ofSeconds(aiProperties.getOllama().getTimeoutSeconds()).toMillis());
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);

        RestClient restClient = RestClient.builder()
                .baseUrl(aiProperties.getOllama().getBaseUrl())
                .requestFactory(requestFactory)
                .build();

        OllamaEmbeddingResponse response = restClient.post()
                .uri("/api/embeddings")
                .body(new OllamaEmbeddingRequest(
                        aiProperties.getOllama().getEmbeddingModel(),
                        truncate(text)
                ))
                .retrieve()
                .body(OllamaEmbeddingResponse.class);

        if (response == null || response.embedding() == null || response.embedding().isEmpty()) {
            throw new IllegalStateException("Ollama returned an empty embedding");
        }
        return response.embedding().stream().mapToDouble(Double::doubleValue).toArray();
    }

    private double[] deterministicEmbedding(String text) {
        int dimension = ragProperties.getPgvector().getEmbeddingDimension();
        double[] vector = new double[dimension];
        byte[] seed = sha256(text == null ? "" : text);
        long randomSeed = 0L;
        for (int i = 0; i < Math.min(8, seed.length); i++) {
            randomSeed = (randomSeed << 8) | (seed[i] & 0xffL);
        }
        Random random = new Random(randomSeed);
        double norm = 0.0;
        for (int i = 0; i < dimension; i++) {
            vector[i] = random.nextDouble() - 0.5;
            norm += vector[i] * vector[i];
        }
        norm = Math.sqrt(norm);
        if (norm == 0.0) {
            return vector;
        }
        for (int i = 0; i < dimension; i++) {
            vector[i] = vector[i] / norm;
        }
        return vector;
    }

    private byte[] sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return digest.digest(value.getBytes(StandardCharsets.UTF_8));
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 is not available", e);
        }
    }

    private String truncate(String value) {
        if (value == null || value.length() <= MAX_INPUT_CHARS) {
            return value;
        }
        return value.substring(0, MAX_INPUT_CHARS);
    }

    private record OllamaEmbeddingRequest(String model, String prompt) {
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private record OllamaEmbeddingResponse(List<Double> embedding) {
    }
}
