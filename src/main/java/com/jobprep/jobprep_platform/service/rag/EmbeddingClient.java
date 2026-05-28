package com.jobprep.jobprep_platform.service.rag;

public interface EmbeddingClient {
    double[] embed(String text);
}
