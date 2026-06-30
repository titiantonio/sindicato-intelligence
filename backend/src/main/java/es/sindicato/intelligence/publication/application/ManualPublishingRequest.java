package es.sindicato.intelligence.publication.application;

import es.sindicato.intelligence.publication.domain.PublicationAttachment;
import es.sindicato.intelligence.publication.domain.PublicationTarget;

import java.util.List;

public record ManualPublishingRequest(
        Long publicationId,
        String channel,
        String title,
        String message,
        PublicationTarget target,
        List<PublicationAttachment> attachments
) {
}
