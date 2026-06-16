package es.sindicato.intelligence.automation.api;

import java.util.List;

public record AutomationRunResponse(
        int processedCount,
        int successCount,
        int failedCount,
        int skippedCount,
        List<AutomationRunErrorResponse> errors
) {
}
