package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.publication.domain.PublicationStatus;

import java.time.OffsetDateTime;

public record PublicationTargetResponse(
        Long id,
        Long destinationId,
        String destinationName,
        PublicationStatus status,
        String externalId,
        String responsePayload,
        OffsetDateTime publishedAt
) {
}
