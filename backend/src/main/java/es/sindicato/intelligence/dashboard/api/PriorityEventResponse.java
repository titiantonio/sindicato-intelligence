package es.sindicato.intelligence.dashboard.api;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;

import java.time.OffsetDateTime;

public record PriorityEventResponse(
        Long id,
        String title,
        EventCategory category,
        Importance importance,
        int relatedNews,
        OffsetDateTime updatedAt,
        EventStatus status
) {
}