package es.sindicato.intelligence.ai.api;

import java.util.List;

public record AiMetricsResponse(
        long totalOperations,
        long successCount,
        long failedCount,
        long averageLatencyMs,
        List<AiMetricResponse> recentMetrics
) {
}
