package es.sindicato.intelligence.publication.infrastructure;

import es.sindicato.intelligence.publication.domain.PublicationAttachment;
import es.sindicato.intelligence.publication.domain.PublicationAttachmentRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class JpaPublicationAttachmentRepository implements PublicationAttachmentRepository {

    private final EntityManager entityManager;

    public JpaPublicationAttachmentRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public PublicationAttachment save(PublicationAttachment attachment) {
        PublicationAttachmentEntity entity = toEntity(attachment);
        if (entity.getId() == null) {
            entityManager.persist(entity);
            return toDomain(entity);
        }
        return toDomain(entityManager.merge(entity));
    }

    @Override
    public List<PublicationAttachment> findByPublicationId(Long publicationId) {
        return entityManager.createQuery(
                        "SELECT attachment FROM PublicationAttachmentEntity attachment WHERE attachment.publicationId = :publicationId ORDER BY attachment.position ASC, attachment.id ASC",
                        PublicationAttachmentEntity.class
                )
                .setParameter("publicationId", publicationId)
                .getResultStream()
                .map(this::toDomain)
                .toList();
    }

    private PublicationAttachmentEntity toEntity(PublicationAttachment attachment) {
        return new PublicationAttachmentEntity(
                attachment.getId(),
                attachment.getPublicationId(),
                attachment.getOriginalFilename(),
                attachment.getMediaType(),
                attachment.getMimeType(),
                attachment.getFileSizeBytes(),
                attachment.getStoragePath(),
                attachment.getTelegramMethod(),
                attachment.getPosition(),
                attachment.getCreatedAt()
        );
    }

    private PublicationAttachment toDomain(PublicationAttachmentEntity entity) {
        return new PublicationAttachment(
                entity.getId(),
                entity.getPublicationId(),
                entity.getOriginalFilename(),
                entity.getMediaType(),
                entity.getMimeType(),
                entity.getFileSizeBytes(),
                entity.getStoragePath(),
                entity.getTelegramMethod(),
                entity.getPosition(),
                entity.getCreatedAt()
        );
    }
}
