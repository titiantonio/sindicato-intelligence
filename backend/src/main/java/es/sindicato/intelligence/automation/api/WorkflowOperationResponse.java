package es.sindicato.intelligence.automation.api;

import java.time.OffsetDateTime;
import java.util.Map;

public record WorkflowOperationResponse(
        String id,
        String workflowCode,
        String operationType,
        String status,
        String relatedEntityType,
        Long relatedEntityId,
        OffsetDateTime createdAt,
        Long latencyMs,
        String promptKey,
        String provider,
        String model,
        String errorMessage,
        Map<String, Object> details
) {
}
