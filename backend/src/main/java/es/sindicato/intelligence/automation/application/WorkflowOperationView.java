package es.sindicato.intelligence.automation.application;

import java.time.OffsetDateTime;
import java.util.Map;

public record WorkflowOperationView(
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
