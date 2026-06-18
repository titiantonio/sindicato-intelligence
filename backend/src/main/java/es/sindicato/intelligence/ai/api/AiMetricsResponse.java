package es.sindicato.intelligence.ai.api;

import java.util.List;

public record AiMetricsResponse(
        long totalOperations,
        long successCount,
        long failedCount,
        long averageLatencyMs,
        long p95LatencyMs,
        long successRate,
        long failureRate,
        long previousTotalOperations,
        long previousSuccessCount,
        long previousFailedCount,
        long previousAverageLatencyMs,
        long totalDifference,
        long successRateDifference,
        long failureRateDifference,
        long averageLatencyDifference,
        List<AiMetricResponse> recentMetrics
) {
}
