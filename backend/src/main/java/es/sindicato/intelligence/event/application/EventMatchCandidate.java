package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;

import java.time.OffsetDateTime;
import java.util.List;

public record EventMatchCandidate(
        Long eventId,
        String title,
        String description,
        EventCategory category,
        EventStatus status,
        OffsetDateTime firstDetectedAt,
        OffsetDateTime lastUpdatedAt,
        int newsCount,
        List<String> recentNewsTitles
) {
    public EventMatchCandidate(Long eventId, String title, String description, EventCategory category) {
        this(eventId, title, description, category, null, null, null, 0, List.of());
    }

    public EventMatchCandidate {
        recentNewsTitles = List.copyOf(recentNewsTitles == null ? List.of() : recentNewsTitles);
    }
}
