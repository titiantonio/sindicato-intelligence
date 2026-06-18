package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;

import java.time.OffsetDateTime;

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
        OffsetDateTime createdAt
) {
}
