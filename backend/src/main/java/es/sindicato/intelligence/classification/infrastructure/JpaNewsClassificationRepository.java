package es.sindicato.intelligence.classification.infrastructure;

import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaNewsClassificationRepository implements NewsClassificationRepository {

    private final EntityManager entityManager;

    public JpaNewsClassificationRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public NewsClassification save(NewsClassification classification) {
        NewsClassificationEntity entity = toEntity(classification);

        if (entity.getId() == null) {
            entityManager.persist(entity);
            return toDomain(entity);
        }

        return toDomain(entityManager.merge(entity));
    }

    @Override
    public Optional<NewsClassification> findById(Long id) {
        return Optional.ofNullable(entityManager.find(NewsClassificationEntity.class, id))
                .map(this::toDomain);
    }

    @Override
    public Optional<NewsClassification> findByNewsId(Long newsId) {
        return entityManager.createQuery(
                        "SELECT classification FROM NewsClassificationEntity classification WHERE classification.newsId = :newsId",
                        NewsClassificationEntity.class
                )
                .setParameter("newsId", newsId)
                .getResultStream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public boolean existsByNewsId(Long newsId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(classification) FROM NewsClassificationEntity classification WHERE classification.newsId = :newsId",
                        Long.class
                )
                .setParameter("newsId", newsId)
                .getSingleResult();

        return count > 0;
    }

    private NewsClassificationEntity toEntity(NewsClassification classification) {
        return new NewsClassificationEntity(
                classification.getId(),
                classification.getNewsId(),
                classification.getCategory(),
                classification.getSubcategory(),
                classification.getRelevanceScore(),
                classification.getImpactLevel(),
                classification.getUrgencyLevel(),
                classification.getKeywords(),
                classification.getEntities(),
                classification.getClassifiedAt()
        );
    }

    private NewsClassification toDomain(NewsClassificationEntity entity) {
        return new NewsClassification(
                entity.getId(),
                entity.getNewsId(),
                entity.getCategory(),
                entity.getSubcategory(),
                entity.getRelevanceScore(),
                entity.getImpactLevel(),
                entity.getUrgencyLevel(),
                entity.getKeywords(),
                entity.getEntities(),
                entity.getClassifiedAt()
        );
    }
}
