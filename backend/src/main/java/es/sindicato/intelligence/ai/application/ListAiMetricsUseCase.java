package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListAiMetricsUseCase {

    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 200;

    private final AiOperationMetricRepository repository;

    public ListAiMetricsUseCase(AiOperationMetricRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public AiMetricsSnapshot execute(Integer requestedLimit) {
        int limit = normalizeLimit(requestedLimit);
        List<AiOperationMetric> metrics = repository.findRecent(limit);
        long total = metrics.size();
        long success = metrics.stream().filter(metric -> metric.getStatus() == AiMetricStatus.SUCCESS).count();
        long failed = metrics.stream().filter(metric -> metric.getStatus() == AiMetricStatus.FAILED).count();
        long averageLatency = total == 0 ? 0 : metrics.stream()
                .mapToLong(AiOperationMetric::getLatencyMs)
                .sum() / total;

        return new AiMetricsSnapshot(
                new AiMetricSummary(total, success, failed, averageLatency),
                metrics.stream().map(this::toView).toList()
        );
    }

    private int normalizeLimit(Integer requestedLimit) {
        if (requestedLimit == null) {
            return DEFAULT_LIMIT;
        }
        return Math.max(1, Math.min(MAX_LIMIT, requestedLimit));
    }

    private AiOperationMetricView toView(AiOperationMetric metric) {
        return new AiOperationMetricView(
                metric.getId(),
                metric.getOperationType(),
                metric.getPromptKey(),
                metric.getProvider(),
                metric.getModel(),
                metric.getStatus(),
                metric.getRelatedEntityType(),
                metric.getRelatedEntityId(),
                metric.getLatencyMs(),
                metric.getErrorMessage(),
                metric.getCreatedAt()
        );
    }
}
