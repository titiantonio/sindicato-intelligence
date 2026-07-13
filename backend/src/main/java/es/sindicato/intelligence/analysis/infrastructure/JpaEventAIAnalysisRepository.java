package es.sindicato.intelligence.analysis.infrastructure;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaEventAIAnalysisRepository implements EventAIAnalysisRepository {

    private final EntityManager entityManager;

    public JpaEventAIAnalysisRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public EventAIAnalysis save(EventAIAnalysis analysis) {
        EventAIAnalysisEntity entity = toEntity(analysis);

        if (entity.getId() == null) {
            entityManager.persist(entity);
            return toDomain(entity);
        }

        return toDomain(entityManager.merge(entity));
    }

    @Override
    public Optional<EventAIAnalysis> findById(Long id) {
        return Optional.ofNullable(entityManager.find(EventAIAnalysisEntity.class, id))
                .map(this::toDomain);
    }

    @Override
    public List<EventAIAnalysis> findByEventId(Long eventId) {
        return entityManager.createQuery(
                        "SELECT analysis FROM EventAIAnalysisEntity analysis WHERE analysis.eventId = :eventId ORDER BY analysis.generatedAt DESC, analysis.id DESC",
                        EventAIAnalysisEntity.class
                )
                .setParameter("eventId", eventId)
                .getResultStream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<EventAIAnalysis> findLatestByEventId(Long eventId) {
        return entityManager.createQuery(
                        "SELECT analysis FROM EventAIAnalysisEntity analysis WHERE analysis.eventId = :eventId ORDER BY analysis.generatedAt DESC, analysis.id DESC",
                        EventAIAnalysisEntity.class
                )
                .setParameter("eventId", eventId)
                .setMaxResults(1)
                .getResultStream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public boolean existsByEventId(Long eventId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(analysis) FROM EventAIAnalysisEntity analysis WHERE analysis.eventId = :eventId",
                        Long.class
                )
                .setParameter("eventId", eventId)
                .getSingleResult();

        return count > 0;
    }

    private EventAIAnalysisEntity toEntity(EventAIAnalysis analysis) {
        return new EventAIAnalysisEntity(
                analysis.getId(),
                analysis.getEventId(),
                analysis.getExecutiveSummary(),
                analysis.getUnionSummary(),
                analysis.getKeyPoints(),
                analysis.getRisks(),
                analysis.getOpportunities(),
                analysis.getAffectedGroups(),
                analysis.getRecommendedMonitoring(),
                analysis.getAnalysisType(),
                analysis.getGenerationTrigger(),
                analysis.getEventUpdatedAtSnapshot(),
                analysis.getContextNewsCount(),
                analysis.isContextTruncated(),
                analysis.getModelUsed(),
                analysis.getGeneratedAt()
        );
    }

    private EventAIAnalysis toDomain(EventAIAnalysisEntity entity) {
        return new EventAIAnalysis(
                entity.getId(),
                entity.getEventId(),
                entity.getExecutiveSummary(),
                entity.getUnionSummary(),
                entity.getKeyPoints() == null ? List.of() : entity.getKeyPoints(),
                entity.getRisks() == null ? List.of() : entity.getRisks(),
                entity.getOpportunities() == null ? List.of() : entity.getOpportunities(),
                entity.getAffectedGroups() == null ? List.of() : entity.getAffectedGroups(),
                entity.getRecommendedMonitoring() == null ? List.of() : entity.getRecommendedMonitoring(),
                entity.getAnalysisType(),
                entity.getGenerationTrigger(),
                entity.getEventUpdatedAtSnapshot(),
                entity.getContextNewsCount(),
                entity.isContextTruncated(),
                entity.getModelUsed(),
                entity.getGeneratedAt()
        );
    }
}
