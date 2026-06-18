package es.sindicato.intelligence.ai.application;

import java.util.List;

public record AiMetricsSnapshot(
        AiMetricSummary summary,
        List<AiOperationMetricView> recentMetrics
) {
}
