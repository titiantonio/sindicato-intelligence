package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.EventCategory;

public record EventMatchCandidate(
        Long eventId,
        String title,
        String description,
        EventCategory category
) {
}
