package es.sindicato.intelligence.ai.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;

@Entity
@Table(name = "ai_operation_metrics")
public class AiOperationMetricEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String operationType;

    @Column(nullable = false)
    private String promptKey;

    @Column(nullable = false)
    private String provider;

    private String model;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AiMetricStatus status;

    private String relatedEntityType;
    private Long relatedEntityId;

    @Column(nullable = false)
    private long latencyMs;

    private String errorMessage;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "operation_details", columnDefinition = "jsonb")
    private JsonNode operationDetails;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    protected AiOperationMetricEntity() {
    }

    public AiOperationMetricEntity(Long id, String operationType, String promptKey, String provider, String model, AiMetricStatus status, String relatedEntityType, Long relatedEntityId, long latencyMs, String errorMessage, JsonNode operationDetails, OffsetDateTime createdAt) {
        this.id = id;
        this.operationType = operationType;
        this.promptKey = promptKey;
        this.provider = provider;
        this.model = model;
        this.status = status;
        this.relatedEntityType = relatedEntityType;
        this.relatedEntityId = relatedEntityId;
        this.latencyMs = latencyMs;
        this.errorMessage = errorMessage;
        this.operationDetails = operationDetails;
        this.createdAt = createdAt;
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

    public JsonNode getOperationDetails() {
        return operationDetails;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
