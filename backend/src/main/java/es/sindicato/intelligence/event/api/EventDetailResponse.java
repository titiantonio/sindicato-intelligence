package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.content.api.GeneratedContentResponse;
import es.sindicato.intelligence.event.application.EventEditorialStatus;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;

import java.time.OffsetDateTime;
import java.util.List;

public record EventDetailResponse(
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
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        List<EventNewsResponse> news,
        List<EventAnalysisResponse> analyses,
        List<GeneratedContentResponse> contents
) {
}
