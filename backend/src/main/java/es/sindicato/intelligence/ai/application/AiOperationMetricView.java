package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;

import java.time.OffsetDateTime;
import java.util.Map;

public record AiOperationMetricView(
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
        Map<String, Object> operationDetails,
        OffsetDateTime createdAt
) {
    public AiOperationMetricView(
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
        this(id, operationType, promptKey, provider, model, status, relatedEntityType, relatedEntityId, latencyMs, errorMessage, Map.of(), createdAt);
    }
}
