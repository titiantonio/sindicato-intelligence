package es.sindicato.intelligence.content.api;

import es.sindicato.intelligence.event.api.EventDetailResponse;

public record GeneratedContentDetailResponse(
        GeneratedContentResponse content,
        EventDetailResponse event
) {
}
