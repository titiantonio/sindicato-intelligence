package es.sindicato.intelligence.ai.api;

import java.time.OffsetDateTime;

public record AiMetricResponse(
        Long id,
        String operationType,
        String promptKey,
        String provider,
        String model,
        String status,
        String relatedEntityType,
        Long relatedEntityId,
        long latencyMs,
        String errorMessage,
        OffsetDateTime createdAt
) {
}
