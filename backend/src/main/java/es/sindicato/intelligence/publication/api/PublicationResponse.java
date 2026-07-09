package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.domain.PublicationStatus;
import es.sindicato.intelligence.publication.domain.PublicationType;

import java.time.OffsetDateTime;
import java.util.List;

public record PublicationResponse(
        Long id,
        Long contentId,
        String channel,
        PublicationType publicationType,
        String titleSnapshot,
        String messageSnapshot,
        Long requestedBy,
        String requestedByName,
        String requestedByEmail,
        String externalId,
        PublicationStatus status,
        OffsetDateTime publishedAt,
        String responsePayload,
        OffsetDateTime scheduledAt,
        List<PublicationTargetResponse> targets,
        List<PublicationAttachmentResponse> attachments
) {
    public PublicationResponse(
            Long id,
            Long contentId,
            String channel,
            String externalId,
            PublicationStatus status,
            OffsetDateTime publishedAt,
            String responsePayload,
            OffsetDateTime scheduledAt
    ) {
        this(id, contentId, channel, PublicationType.GENERATED_CONTENT, null, null, null, null, null, externalId, status, publishedAt, responsePayload, scheduledAt, List.of(), List.of());
    }
}
