package es.sindicato.intelligence.ai.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Objects;

public class AiWorkflowSetting {

    private final String workflowCode;
    private String providerCode;
    private String modelName;
    private BigDecimal temperature;
    private int maxOutputTokens;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public AiWorkflowSetting(String workflowCode, String providerCode, String modelName, BigDecimal temperature, int maxOutputTokens, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.workflowCode = requireText(workflowCode, "workflowCode is required");
        this.providerCode = requireText(providerCode, "providerCode is required");
        this.modelName = requireText(modelName, "modelName is required");
        this.temperature = validateTemperature(temperature);
        this.maxOutputTokens = validateMaxOutputTokens(maxOutputTokens);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public void update(String providerCode, String modelName, BigDecimal temperature, int maxOutputTokens, OffsetDateTime now) {
        this.providerCode = requireText(providerCode, "providerCode is required");
        this.modelName = requireText(modelName, "modelName is required");
        this.temperature = validateTemperature(temperature);
        this.maxOutputTokens = validateMaxOutputTokens(maxOutputTokens);
        this.updatedAt = Objects.requireNonNull(now, "now is required");
    }

    public String getWorkflowCode() {
        return workflowCode;
    }

    public String getProviderCode() {
        return providerCode;
    }

    public String getModelName() {
        return modelName;
    }

    public BigDecimal getTemperature() {
        return temperature;
    }

    public int getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private String requireText(String value, String message) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private BigDecimal validateTemperature(BigDecimal value) {
        BigDecimal resolved = value == null ? BigDecimal.valueOf(0.2) : value;
        if (resolved.compareTo(BigDecimal.ZERO) < 0 || resolved.compareTo(BigDecimal.valueOf(2)) > 0) {
            throw new IllegalArgumentException("temperature must be between 0 and 2");
        }
        return resolved;
    }

    private int validateMaxOutputTokens(int value) {
        if (value < 1) {
            throw new IllegalArgumentException("maxOutputTokens must be at least 1");
        }
        return value;
    }
}
