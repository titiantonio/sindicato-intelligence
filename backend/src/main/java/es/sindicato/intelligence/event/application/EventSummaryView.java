package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;

import java.time.OffsetDateTime;

public record EventSummaryView(
        Long id,
        String title,
        String description,
        EventCategory category,
        Importance importance,
        EventStatus status,
        EventEditorialStatus editorialStatus,
        int newsCount,
        OffsetDateTime firstDetectedAt,
        OffsetDateTime lastUpdatedAt,
        OffsetDateTime updatedAt
) {
}
