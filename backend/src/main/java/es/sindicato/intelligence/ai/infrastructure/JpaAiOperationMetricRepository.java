package es.sindicato.intelligence.ai.infrastructure;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Repository
public class JpaAiOperationMetricRepository implements AiOperationMetricRepository {

    private static final TypeReference<Map<String, Object>> DETAILS_TYPE = new TypeReference<>() {
    };

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public JpaAiOperationMetricRepository(EntityManager entityManager, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public AiOperationMetric save(AiOperationMetric metric) {
        AiOperationMetricEntity entity = new AiOperationMetricEntity(
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
                toJsonNode(metric.getOperationDetails()),
                metric.getCreatedAt()
        );
        return toDomain(entityManager.merge(entity));
    }

    @Override
    public List<AiOperationMetric> findRecent(int limit) {
        return entityManager.createQuery(
                        """
                        SELECT metric
                        FROM AiOperationMetricEntity metric
                        ORDER BY metric.createdAt DESC, metric.id DESC
                        """,
                        AiOperationMetricEntity.class
                )
                .setMaxResults(limit)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<AiOperationMetric> findByCreatedAtBetween(OffsetDateTime fromInclusive, OffsetDateTime toExclusive) {
        return entityManager.createQuery(
                        """
                        SELECT metric
                        FROM AiOperationMetricEntity metric
                        WHERE metric.createdAt >= :fromInclusive
                          AND metric.createdAt < :toExclusive
                        ORDER BY metric.createdAt DESC, metric.id DESC
                        """,
                        AiOperationMetricEntity.class
                )
                .setParameter("fromInclusive", fromInclusive)
                .setParameter("toExclusive", toExclusive)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public long countByPromptKeyAndRelatedEntityAndStatusSince(
            String promptKey,
            String relatedEntityType,
            Long relatedEntityId,
            AiMetricStatus status,
            OffsetDateTime since
    ) {
        return entityManager.createQuery(
                        """
                        SELECT COUNT(metric)
                        FROM AiOperationMetricEntity metric
                        WHERE metric.promptKey = :promptKey
                          AND metric.relatedEntityType = :relatedEntityType
                          AND metric.relatedEntityId = :relatedEntityId
                          AND metric.status = :status
                          AND metric.createdAt >= :since
                        """,
                        Long.class
                )
                .setParameter("promptKey", promptKey)
                .setParameter("relatedEntityType", relatedEntityType)
                .setParameter("relatedEntityId", relatedEntityId)
                .setParameter("status", status)
                .setParameter("since", since)
                .getSingleResult();
    }

    private AiOperationMetric toDomain(AiOperationMetricEntity entity) {
        return new AiOperationMetric(
                entity.getId(),
                entity.getOperationType(),
                entity.getPromptKey(),
                entity.getProvider(),
                entity.getModel(),
                entity.getStatus(),
                entity.getRelatedEntityType(),
                entity.getRelatedEntityId(),
                entity.getLatencyMs(),
                entity.getErrorMessage(),
                toMap(entity.getOperationDetails()),
                entity.getCreatedAt()
        );
    }

    private JsonNode toJsonNode(Map<String, Object> details) {
        if (details == null || details.isEmpty()) {
            return null;
        }
        return objectMapper.valueToTree(details);
    }

    private Map<String, Object> toMap(JsonNode details) {
        if (details == null || details.isNull()) {
            return Map.of();
        }
        return objectMapper.convertValue(details, DETAILS_TYPE);
    }
}
