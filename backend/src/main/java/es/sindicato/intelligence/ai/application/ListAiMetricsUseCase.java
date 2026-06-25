package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class ListAiMetricsUseCase {

    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 200;
    private static final ZoneId OPERATIVE_ZONE = ZoneId.of("Europe/Madrid");

    private final AiOperationMetricRepository repository;

    public ListAiMetricsUseCase(AiOperationMetricRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public AiMetricsSnapshot execute(Integer requestedLimit) {
        int limit = normalizeLimit(requestedLimit);
        List<AiOperationMetric> metrics = repository.findRecent(limit);

        return new AiMetricsSnapshot(
                summarize(metrics),
                metrics.stream().map(this::toView).toList()
        );
    }

    @Transactional(readOnly = true)
    public AiMetricsSnapshot execute(LocalDate date) {
        LocalDate selectedDate = date == null ? LocalDate.now(OPERATIVE_ZONE) : date;
        List<AiOperationMetric> currentMetrics = repository.findByCreatedAtBetween(
                startOfDay(selectedDate),
                startOfDay(selectedDate.plusDays(1))
        );
        List<AiOperationMetric> previousMetrics = repository.findByCreatedAtBetween(
                startOfDay(selectedDate.minusDays(1)),
                startOfDay(selectedDate)
        );

        return new AiMetricsSnapshot(
                summarizeDaily(currentMetrics, previousMetrics),
                currentMetrics.stream().map(this::toView).toList()
        );
    }

    private int normalizeLimit(Integer requestedLimit) {
        if (requestedLimit == null) {
            return DEFAULT_LIMIT;
        }
        return Math.max(1, Math.min(MAX_LIMIT, requestedLimit));
    }

    private OffsetDateTime startOfDay(LocalDate date) {
        return date.atStartOfDay(OPERATIVE_ZONE).toOffsetDateTime();
    }

    private AiMetricSummary summarize(List<AiOperationMetric> metrics) {
        long total = metrics.size();
        long success = countStatus(metrics, AiMetricStatus.SUCCESS);
        long failed = countStatus(metrics, AiMetricStatus.FAILED);
        long averageLatency = averageLatency(metrics);
        return new AiMetricSummary(total, success, failed, averageLatency);
    }

    private AiMetricSummary summarizeDaily(List<AiOperationMetric> currentMetrics, List<AiOperationMetric> previousMetrics) {
        long total = currentMetrics.size();
        long success = countStatus(currentMetrics, AiMetricStatus.SUCCESS);
        long failed = countStatus(currentMetrics, AiMetricStatus.FAILED);
        long averageLatency = averageLatency(currentMetrics);
        long successRate = percentage(success, total);
        long failureRate = percentage(failed, total);

        long previousTotal = previousMetrics.size();
        long previousSuccess = countStatus(previousMetrics, AiMetricStatus.SUCCESS);
        long previousFailed = countStatus(previousMetrics, AiMetricStatus.FAILED);
        long previousAverageLatency = averageLatency(previousMetrics);
        long previousSuccessRate = percentage(previousSuccess, previousTotal);
        long previousFailureRate = percentage(previousFailed, previousTotal);

        return new AiMetricSummary(
                total,
                success,
                failed,
                averageLatency,
                p95Latency(currentMetrics),
                successRate,
                failureRate,
                previousTotal,
                previousSuccess,
                previousFailed,
                previousAverageLatency,
                total - previousTotal,
                successRate - previousSuccessRate,
                failureRate - previousFailureRate,
                averageLatency - previousAverageLatency
        );
    }

    private long countStatus(List<AiOperationMetric> metrics, AiMetricStatus status) {
        return metrics.stream().filter(metric -> metric.getStatus() == status).count();
    }

    private long averageLatency(List<AiOperationMetric> metrics) {
        long total = metrics.size();
        if (total == 0) {
            return 0;
        }
        return metrics.stream().mapToLong(AiOperationMetric::getLatencyMs).sum() / total;
    }

    private long p95Latency(List<AiOperationMetric> metrics) {
        if (metrics.isEmpty()) {
            return 0;
        }
        List<Long> sortedLatencies = metrics.stream()
                .map(AiOperationMetric::getLatencyMs)
                .sorted()
                .toList();
        int index = (int) Math.ceil(sortedLatencies.size() * 0.95) - 1;
        return sortedLatencies.get(Math.max(0, index));
    }

    private long percentage(long value, long total) {
        if (total == 0) {
            return 0;
        }
        return Math.round((value * 100.0) / total);
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
                metric.getOperationDetails(),
                metric.getCreatedAt()
        );
    }
}
