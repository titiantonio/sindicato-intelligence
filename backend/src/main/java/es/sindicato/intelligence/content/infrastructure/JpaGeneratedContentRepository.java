package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaGeneratedContentRepository implements GeneratedContentRepository {

    private final EntityManager entityManager;

    public JpaGeneratedContentRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public GeneratedContent save(GeneratedContent content) {
        GeneratedContentEntity entity = toEntity(content);

        if (entity.getId() == null) {
            entityManager.persist(entity);
            return toDomain(entity);
        }

        return toDomain(entityManager.merge(entity));
    }

    @Override
    public Optional<GeneratedContent> findById(Long id) {
        return Optional.ofNullable(entityManager.find(GeneratedContentEntity.class, id))
                .map(this::toDomain);
    }

    @Override
    public List<GeneratedContent> findAll() {
        return entityManager.createQuery(
                        "SELECT content FROM GeneratedContentEntity content ORDER BY content.generatedAt DESC, content.id DESC",
                        GeneratedContentEntity.class
                )
                .getResultStream()
                .map(this::toDomain)
                .toList();
    }
    @Override
    public List<GeneratedContent> findByEventId(Long eventId) {
        return entityManager.createQuery(
                        "SELECT content FROM GeneratedContentEntity content WHERE content.eventId = :eventId ORDER BY content.generatedAt DESC, content.id DESC",
                        GeneratedContentEntity.class
                )
                .setParameter("eventId", eventId)
                .getResultStream()
                .map(this::toDomain)
                .toList();
    }

    private GeneratedContentEntity toEntity(GeneratedContent content) {
        return new GeneratedContentEntity(
                content.getId(),
                content.getEventId(),
                content.getAnalysisId(),
                content.getCreatedBy(),
                content.getChannel(),
                content.getTone(),
                content.getTitle(),
                content.getContent(),
                content.getStatus(),
                content.getGeneratedAt(),
                content.getApprovedAt()
        );
    }

    private GeneratedContent toDomain(GeneratedContentEntity entity) {
        return new GeneratedContent(
                entity.getId(),
                entity.getEventId(),
                entity.getAnalysisId(),
                entity.getCreatedBy(),
                entity.getChannel(),
                entity.getTone(),
                entity.getTitle(),
                entity.getContent(),
                entity.getStatus(),
                entity.getGeneratedAt(),
                entity.getApprovedAt()
        );
    }
}
