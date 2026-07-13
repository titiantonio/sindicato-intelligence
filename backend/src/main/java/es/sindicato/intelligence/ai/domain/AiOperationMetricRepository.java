package es.sindicato.intelligence.ai.domain;

import java.util.List;
import java.time.OffsetDateTime;

public interface AiOperationMetricRepository {

    AiOperationMetric save(AiOperationMetric metric);

    List<AiOperationMetric> findRecent(int limit);

    List<AiOperationMetric> findByCreatedAtBetween(OffsetDateTime fromInclusive, OffsetDateTime toExclusive);

    long countByPromptKeyAndRelatedEntityAndStatusSince(
            String promptKey,
            String relatedEntityType,
            Long relatedEntityId,
            AiMetricStatus status,
            OffsetDateTime since
    );
}
