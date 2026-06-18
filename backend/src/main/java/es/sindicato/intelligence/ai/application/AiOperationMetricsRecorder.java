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
        record(operationType, promptKey, provider, model, AiMetricStatus.SUCCESS, relatedEntityType, relatedEntityId, startedAt, null);
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
        record(operationType, promptKey, provider, model, AiMetricStatus.FAILED, relatedEntityType, relatedEntityId, startedAt, exception.getMessage());
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
            String errorMessage
    ) {
        try {
            OffsetDateTime now = OffsetDateTime.now(clock);
            repository.save(new AiOperationMetric(
                    null,
                    operationType,
                    promptKey,
                    provider,
                    model,
                    status,
                    relatedEntityType,
                    relatedEntityId,
                    Duration.between(startedAt, now).toMillis(),
                    errorMessage,
                    now
            ));
        } catch (RuntimeException exception) {
            log.warn("ai metric could not be recorded: operationType={}, promptKey={}, reason={}", operationType, promptKey, exception.getMessage());
        }
    }
}
