package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.event.application.EventEditorialStatus;

import java.time.OffsetDateTime;

public record EventSummaryResponse(
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
