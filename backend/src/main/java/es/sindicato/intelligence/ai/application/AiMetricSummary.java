package es.sindicato.intelligence.ai.application;

public record AiMetricSummary(
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
        long averageLatencyDifference
) {
    public AiMetricSummary(long totalOperations, long successCount, long failedCount, long averageLatencyMs) {
        this(
                totalOperations,
                successCount,
                failedCount,
                averageLatencyMs,
                0,
                percentage(successCount, totalOperations),
                percentage(failedCount, totalOperations),
                0,
                0,
                0,
                0,
                0,
                0,
                0,
                0
        );
    }

    private static long percentage(long value, long total) {
        if (total == 0) {
            return 0;
        }
        return Math.round((value * 100.0) / total);
    }
}
