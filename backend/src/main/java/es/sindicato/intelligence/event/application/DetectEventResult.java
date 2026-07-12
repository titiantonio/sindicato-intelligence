package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.EventMatchDecision;
import es.sindicato.intelligence.event.domain.EventStatus;

public record DetectEventResult(
        Long eventId,
        Long newsId,
        boolean created,
        boolean matched,
        int confidence,
        String reason,
        EventStatus eventStatus,
        EventMatchDecision matchDecision
) {
}
