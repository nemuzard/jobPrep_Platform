package com.jobprep.jobprep_platform.service.rag.impl;

import com.jobprep.jobprep_platform.config.rag.RagProperties;
import com.jobprep.jobprep_platform.model.entity.rag.LearningResourceDocument;
import com.jobprep.jobprep_platform.model.entity.rag.LearningResourceHit;
import com.jobprep.jobprep_platform.service.rag.LearningResourceVectorStore;
import com.jobprep.jobprep_platform.utils.resumematch.HashUtils;
import lombok.extern.log4j.Log4j2;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Log4j2
@Service
public class PgvectorLearningResourceVectorStore implements LearningResourceVectorStore {
    private final RagProperties properties;
    private final JdbcTemplate jdbcTemplate;
    private final AtomicBoolean schemaReady = new AtomicBoolean(false);

    public PgvectorLearningResourceVectorStore(RagProperties properties) {
        this.properties = properties;
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(properties.getPgvector().getUrl());
        dataSource.setUsername(properties.getPgvector().getUsername());
        dataSource.setPassword(properties.getPgvector().getPassword());
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void upsert(LearningResourceDocument document, double[] embedding) {
        ensureSchema();
        String table = tableName();
        String sql = """
                INSERT INTO %s (
                    resource_type,
                    resource_id,
                    title,
                    content,
                    content_hash,
                    metadata_json,
                    embedding
                ) VALUES (?, ?, ?, ?, ?, ?, ?::vector)
                ON CONFLICT (resource_type, resource_id)
                DO UPDATE SET
                    title = EXCLUDED.title,
                    content = EXCLUDED.content,
                    content_hash = EXCLUDED.content_hash,
                    metadata_json = EXCLUDED.metadata_json,
                    embedding = EXCLUDED.embedding,
                    updated_at = NOW()
                """.formatted(table);
        jdbcTemplate.update(
                sql,
                document.getResourceType(),
                document.getResourceId(),
                document.getTitle(),
                document.getContent(),
                HashUtils.sha256(document.getContent()),
                document.getMetadataJson(),
                toVectorLiteral(embedding)
        );
    }

    @Override
    public List<LearningResourceHit> search(double[] queryEmbedding, int topK) {
        ensureSchema();
        String vector = toVectorLiteral(queryEmbedding);
        String sql = """
                SELECT
                    resource_type,
                    resource_id,
                    title,
                    content,
                    metadata_json,
                    1 - (embedding <=> ?::vector) AS similarity
                FROM %s
                ORDER BY embedding <=> ?::vector
                LIMIT ?
                """.formatted(tableName());
        return jdbcTemplate.query(sql, (rs, rowNum) -> toHit(rs), vector, vector, topK);
    }

    private void ensureSchema() {
        if (schemaReady.get()) {
            return;
        }
        synchronized (schemaReady) {
            if (schemaReady.get()) {
                return;
            }
            String table = tableName();
            jdbcTemplate.execute("CREATE EXTENSION IF NOT EXISTS vector");
            jdbcTemplate.execute("""
                    CREATE TABLE IF NOT EXISTS %s (
                        id BIGSERIAL PRIMARY KEY,
                        resource_type VARCHAR(32) NOT NULL,
                        resource_id BIGINT NOT NULL,
                        title TEXT NOT NULL,
                        content TEXT NOT NULL,
                        content_hash CHAR(64) NOT NULL,
                        metadata_json TEXT,
                        embedding vector(%d) NOT NULL,
                        created_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        updated_at TIMESTAMP NOT NULL DEFAULT NOW(),
                        UNIQUE (resource_type, resource_id)
                    )
                    """.formatted(table, properties.getPgvector().getEmbeddingDimension()));
            try {
                jdbcTemplate.execute("""
                        CREATE INDEX IF NOT EXISTS idx_%s_embedding_hnsw
                        ON %s USING hnsw (embedding vector_cosine_ops)
                        """.formatted(table, table));
            } catch (Exception e) {
                log.warn("failed to create HNSW index; pgvector search still works with sequential scan", e);
            }
            schemaReady.set(true);
        }
    }

    private LearningResourceHit toHit(ResultSet rs) throws SQLException {
        LearningResourceHit hit = new LearningResourceHit();
        hit.setResourceType(rs.getString("resource_type"));
        hit.setResourceId(rs.getLong("resource_id"));
        hit.setTitle(rs.getString("title"));
        hit.setContent(rs.getString("content"));
        hit.setMetadataJson(rs.getString("metadata_json"));
        hit.setSimilarity(rs.getDouble("similarity"));
        return hit;
    }

    private String toVectorLiteral(double[] embedding) {
        return "[" + java.util.Arrays.stream(embedding)
                .mapToObj(value -> String.format(java.util.Locale.US, "%.8f", value))
                .collect(Collectors.joining(",")) + "]";
    }

    private String tableName() {
        String table = properties.getPgvector().getTableName();
        if (!table.matches("[A-Za-z_][A-Za-z0-9_]*")) {
            throw new IllegalArgumentException("Invalid pgvector table name");
        }
        return table;
    }
}
