package es.sindicato.intelligence.ai.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_workflow_settings")
public class AiWorkflowSettingEntity {

    @Id
    @Column(name = "workflow_code", nullable = false, length = 80)
    private String workflowCode;

    @Column(name = "provider_code", nullable = false, length = 50)
    private String providerCode;

    @Column(name = "model_name", nullable = false, length = 160)
    private String modelName;

    @Column(nullable = false, precision = 4, scale = 3)
    private BigDecimal temperature;

    @Column(name = "max_output_tokens", nullable = false)
    private int maxOutputTokens;

    @Column(name = "cooldown_seconds", nullable = false)
    private int cooldownSeconds;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected AiWorkflowSettingEntity() {
    }

    public AiWorkflowSettingEntity(String workflowCode, String providerCode, String modelName, BigDecimal temperature, int maxOutputTokens, int cooldownSeconds, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.workflowCode = workflowCode;
        this.providerCode = providerCode;
        this.modelName = modelName;
        this.temperature = temperature;
        this.maxOutputTokens = maxOutputTokens;
        this.cooldownSeconds = cooldownSeconds;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public int getCooldownSeconds() {
        return cooldownSeconds;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
