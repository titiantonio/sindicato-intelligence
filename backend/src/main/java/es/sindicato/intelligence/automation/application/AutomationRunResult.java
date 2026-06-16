package es.sindicato.intelligence.automation.application;

import java.util.List;

public record AutomationRunResult(
        int processedCount,
        int successCount,
        int failedCount,
        int skippedCount,
        List<AutomationRunError> errors
) {

    public static AutomationRunResult empty() {
        return new AutomationRunResult(0, 0, 0, 0, List.of());
    }
}
