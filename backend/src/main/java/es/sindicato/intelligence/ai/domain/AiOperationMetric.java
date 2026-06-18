package es.sindicato.intelligence.ai.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class AiOperationMetric {

    private final Long id;
    private final String operationType;
    private final String promptKey;
    private final String provider;
    private final String model;
    private final AiMetricStatus status;
    private final String relatedEntityType;
    private final Long relatedEntityId;
    private final long latencyMs;
    private final String errorMessage;
    private final OffsetDateTime createdAt;

    public AiOperationMetric(
            Long id,
            String operationType,
            String promptKey,
            String provider,
            String model,
            AiMetricStatus status,
            String relatedEntityType,
            Long relatedEntityId,
            long latencyMs,
            String errorMessage,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.operationType = requireText(operationType, "operationType is required");
        this.promptKey = requireText(promptKey, "promptKey is required");
        this.provider = requireText(provider, "provider is required");
        this.model = normalize(model);
        this.status = Objects.requireNonNull(status, "status is required");
        this.relatedEntityType = normalize(relatedEntityType);
        this.relatedEntityId = relatedEntityId;
        this.latencyMs = Math.max(0, latencyMs);
        this.errorMessage = truncate(errorMessage);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public Long getId() {
        return id;
    }

    public String getOperationType() {
        return operationType;
    }

    public String getPromptKey() {
        return promptKey;
    }

    public String getProvider() {
        return provider;
    }

    public String getModel() {
        return model;
    }

    public AiMetricStatus getStatus() {
        return status;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public Long getRelatedEntityId() {
        return relatedEntityId;
    }

    public long getLatencyMs() {
        return latencyMs;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    private String requireText(String value, String message) {
        String normalized = normalize(value);
        if (normalized == null) {
            throw new IllegalArgumentException(message);
        }
        return normalized;
    }

    private String normalize(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String truncate(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        return normalized.length() <= 500 ? normalized : normalized.substring(0, 500);
    }
}
