package es.sindicato.intelligence.publication.domain;

import java.util.List;

public interface PublicationAttachmentRepository {

    PublicationAttachment save(PublicationAttachment attachment);

    List<PublicationAttachment> findByPublicationId(Long publicationId);
}
