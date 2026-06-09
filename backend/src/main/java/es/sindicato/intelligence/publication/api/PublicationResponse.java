package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.domain.PublicationStatus;

import java.time.OffsetDateTime;

public record PublicationResponse(
        Long id,
        Long contentId,
        String channel,
        String externalId,
        PublicationStatus status,
        OffsetDateTime publishedAt,
        String responsePayload
) {
}
