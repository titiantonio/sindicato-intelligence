package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.event.domain.EventStatus;

public record DetectEventResponse(
        Long eventId,
        Long newsId,
        boolean created,
        boolean matched,
        int confidence,
        String reason,
        EventStatus eventStatus
) {
}
