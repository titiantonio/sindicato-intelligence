package es.sindicato.intelligence.publication.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.publication.domain.Publication;
import es.sindicato.intelligence.publication.domain.PublicationRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class JpaPublicationRepository implements PublicationRepository {

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public JpaPublicationRepository(EntityManager entityManager, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public Publication save(Publication publication) {
        PublicationEntity entity = toEntity(publication);

        if (entity.getId() == null) {
            entityManager.persist(entity);
            return toDomain(entity);
        }

        return toDomain(entityManager.merge(entity));
    }

    @Override
    public Optional<Publication> findById(Long id) {
        return Optional.ofNullable(entityManager.find(PublicationEntity.class, id))
                .map(this::toDomain);
    }

    @Override
    public List<Publication> findByContentId(Long contentId) {
        return entityManager.createQuery(
                        "SELECT publication FROM PublicationEntity publication WHERE publication.contentId = :contentId ORDER BY publication.id DESC",
                        PublicationEntity.class
                )
                .setParameter("contentId", contentId)
                .getResultStream()
                .map(this::toDomain)
                .toList();
    }

    private PublicationEntity toEntity(Publication publication) {
        return new PublicationEntity(
                publication.getId(),
                publication.getContentId(),
                publication.getChannel(),
                publication.getExternalId(),
                publication.getStatus(),
                publication.getPublishedAt(),
                toJsonNode(publication.getResponsePayload())
        );
    }

    private Publication toDomain(PublicationEntity entity) {
        return new Publication(
                entity.getId(),
                entity.getContentId(),
                entity.getChannel(),
                entity.getExternalId(),
                entity.getStatus(),
                entity.getPublishedAt(),
                entity.getResponsePayload() == null ? null : entity.getResponsePayload().toString()
        );
    }

    private JsonNode toJsonNode(String responsePayload) {
        if (responsePayload == null || responsePayload.isBlank()) {
            return null;
        }

        try {
            return objectMapper.readTree(responsePayload);
        } catch (JsonProcessingException exception) {
            throw new IllegalArgumentException("responsePayload must be valid JSON", exception);
        }
    }
}
