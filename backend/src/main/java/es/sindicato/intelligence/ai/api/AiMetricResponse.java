package es.sindicato.intelligence.ai.api;

import java.time.OffsetDateTime;
import java.util.Map;

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
        Map<String, Object> operationDetails,
        OffsetDateTime createdAt
) {
}
