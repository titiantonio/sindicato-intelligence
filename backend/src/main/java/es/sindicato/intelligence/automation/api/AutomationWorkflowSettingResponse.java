package es.sindicato.intelligence.automation.api;

import java.time.OffsetDateTime;

public record AutomationWorkflowSettingResponse(
        String workflowCode,
        boolean enabled,
        int intervalSeconds,
        int batchSize,
        boolean running,
        OffsetDateTime lastRunAt,
        OffsetDateTime lastSuccessAt,
        OffsetDateTime lastFailureAt,
        OffsetDateTime nextRunAt,
        int lastProcessedCount,
        int lastSuccessCount,
        int lastFailedCount,
        int lastSkippedCount,
        String lastError
) {
}
