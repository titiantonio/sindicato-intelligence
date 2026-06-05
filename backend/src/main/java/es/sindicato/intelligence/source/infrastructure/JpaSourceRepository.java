package es.sindicato.intelligence.source.infrastructure;

import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaSourceRepository implements SourceRepository {

    private final EntityManager entityManager;

    public JpaSourceRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Source save(Source source) {
        SourceEntity entity = toEntity(source);

        if (entity.getId() == null) {
            entityManager.persist(entity);
            return toDomain(entity);
        }

        return toDomain(entityManager.merge(entity));
    }

    @Override
    public Optional<Source> findById(Long id) {
        return Optional.ofNullable(entityManager.find(SourceEntity.class, id))
                .map(this::toDomain);
    }

    @Override
    public Optional<Source> findByUrl(String url) {
        return entityManager.createQuery(
                        "SELECT source FROM SourceEntity source WHERE source.url = :url",
                        SourceEntity.class
                )
                .setParameter("url", url)
                .getResultStream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public List<Source> findAll() {
        return entityManager.createQuery(
                        "SELECT source FROM SourceEntity source ORDER BY source.priority ASC, source.name ASC",
                        SourceEntity.class
                )
                .getResultStream()
                .map(this::toDomain)
                .toList();
    }

    private SourceEntity toEntity(Source source) {
        return new SourceEntity(
                source.getId(),
                source.getName(),
                source.getUrl(),
                source.getType(),
                source.getPriority(),
                source.isActive(),
                source.getCreatedAt(),
                source.getUpdatedAt()
        );
    }

    private Source toDomain(SourceEntity entity) {
        return new Source(
                entity.getId(),
                entity.getName(),
                entity.getUrl(),
                entity.getType(),
                entity.getPriority(),
                entity.getActive(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
