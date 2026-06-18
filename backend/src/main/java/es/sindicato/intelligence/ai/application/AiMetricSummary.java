package es.sindicato.intelligence.ai.application;

public record AiMetricSummary(
        long totalOperations,
        long successCount,
        long failedCount,
        long averageLatencyMs
) {
}
