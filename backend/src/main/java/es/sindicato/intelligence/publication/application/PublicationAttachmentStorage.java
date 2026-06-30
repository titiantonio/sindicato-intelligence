package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.publication.domain.PublicationAttachment;

import java.util.List;

public interface PublicationAttachmentStorage {

    List<PublicationAttachment> store(Long publicationId, List<ManualPublicationFile> files);
}
