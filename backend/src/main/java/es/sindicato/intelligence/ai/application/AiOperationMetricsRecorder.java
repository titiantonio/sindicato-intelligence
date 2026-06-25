package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Map;

@Service
public class AiOperationMetricsRecorder {

    private static final Logger log = LoggerFactory.getLogger(AiOperationMetricsRecorder.class);

    private final AiOperationMetricRepository repository;
    private final Clock clock;

    public AiOperationMetricsRecorder(AiOperationMetricRepository repository, Clock clock) {
        this.repository = repository;
        this.clock = clock;
    }

    public OffsetDateTime start() {
        return OffsetDateTime.now(clock);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSuccess(
            String operationType,
            String promptKey,
            String provider,
            String model,
            String relatedEntityType,
            Long relatedEntityId,
            OffsetDateTime startedAt
    ) {
        recordSuccess(operationType, promptKey, provider, model, relatedEntityType, relatedEntityId, startedAt, Map.of());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordSuccess(
            String operationType,
            String promptKey,
            String provider,
            String model,
            String relatedEntityType,
            Long relatedEntityId,
            OffsetDateTime startedAt,
            Map<String, Object> operationDetails
    ) {
        record(operationType, promptKey, provider, model, AiMetricStatus.SUCCESS, relatedEntityType, relatedEntityId, startedAt, null, operationDetails);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(
            String operationType,
            String promptKey,
            String provider,
            String model,
            String relatedEntityType,
            Long relatedEntityId,
            OffsetDateTime startedAt,
            RuntimeException exception
    ) {
        recordFailure(operationType, promptKey, provider, model, relatedEntityType, relatedEntityId, startedAt, exception, Map.of());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void recordFailure(
            String operationType,
            String promptKey,
            String provider,
            String model,
            String relatedEntityType,
            Long relatedEntityId,
            OffsetDateTime startedAt,
            RuntimeException exception,
            Map<String, Object> operationDetails
    ) {
        record(operationType, promptKey, provider, model, AiMetricStatus.FAILED, relatedEntityType, relatedEntityId, startedAt, AiErrorSanitizer.metricMessage(exception.getMessage()), operationDetails);
    }

    private void record(
            String operationType,
            String promptKey,
            String provider,
            String model,
            AiMetricStatus status,
            String relatedEntityType,
            Long relatedEntityId,
            OffsetDateTime startedAt,
            String errorMessage,
            Map<String, Object> operationDetails
    ) {
        try {
            OffsetDateTime now = OffsetDateTime.now(clock);
            OffsetDateTime start = startedAt == null ? now : startedAt;
            repository.save(new AiOperationMetric(
                    null,
                    operationType,
                    promptKey,
                    provider,
                    model,
                    status,
                    relatedEntityType,
                    relatedEntityId,
                    Duration.between(start, now).toMillis(),
                    errorMessage,
                    operationDetails,
                    now
            ));
        } catch (RuntimeException exception) {
            log.warn("ai metric could not be recorded: operationType={}, promptKey={}, reason={}", operationType, promptKey, exception.getMessage());
        }
    }
}
