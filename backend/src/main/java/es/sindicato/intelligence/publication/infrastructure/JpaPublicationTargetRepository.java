package es.sindicato.intelligence.publication.infrastructure;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import es.sindicato.intelligence.publication.domain.PublicationTarget;
import es.sindicato.intelligence.publication.domain.PublicationTargetRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaPublicationTargetRepository implements PublicationTargetRepository {

    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public JpaPublicationTargetRepository(EntityManager entityManager, ObjectMapper objectMapper) {
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    @Override
    public PublicationTarget save(PublicationTarget target) {
        PublicationTargetEntity entity = toEntity(target);
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return toDomain(entity);
        }
        return toDomain(entityManager.merge(entity));
    }

    @Override
    public List<PublicationTarget> findByPublicationId(Long publicationId) {
        return entityManager.createQuery(
                        "SELECT target FROM PublicationTargetEntity target WHERE target.publicationId = :publicationId ORDER BY target.id ASC",
                        PublicationTargetEntity.class
                )
                .setParameter("publicationId", publicationId)
                .getResultStream()
                .map(this::toDomain)
                .toList();
    }

    private PublicationTargetEntity toEntity(PublicationTarget target) {
        return new PublicationTargetEntity(
                target.getId(),
                target.getPublicationId(),
                target.getChannel(),
                target.getDestinationId(),
                target.getDestinationName(),
                target.getDestinationAddress(),
                target.getStatus(),
                target.getExternalId(),
                toJsonNode(target.getResponsePayload()),
                target.getPublishedAt(),
                target.getCreatedAt()
        );
    }

    private PublicationTarget toDomain(PublicationTargetEntity entity) {
        JsonNode responsePayload = entity.getResponsePayload();
        return new PublicationTarget(
                entity.getId(),
                entity.getPublicationId(),
                entity.getChannel(),
                entity.getDestinationId(),
                entity.getDestinationName(),
                entity.getDestinationAddress(),
                entity.getStatus(),
                entity.getExternalId(),
                responsePayload == null ? null : responsePayload.toString(),
                entity.getPublishedAt(),
                entity.getCreatedAt()
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
