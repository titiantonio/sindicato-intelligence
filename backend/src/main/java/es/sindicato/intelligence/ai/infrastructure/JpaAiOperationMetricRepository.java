package es.sindicato.intelligence.ai.infrastructure;

import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaAiOperationMetricRepository implements AiOperationMetricRepository {

    private final EntityManager entityManager;

    public JpaAiOperationMetricRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
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
                entity.getCreatedAt()
        );
    }
}
