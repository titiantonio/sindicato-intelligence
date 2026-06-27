package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.content.api.GeneratedContentResponse;
import es.sindicato.intelligence.event.api.EventDetailResponse;

public record PublicationDetailResponse(
        PublicationResponse publication,
        GeneratedContentResponse content,
        EventDetailResponse event
) {
}
