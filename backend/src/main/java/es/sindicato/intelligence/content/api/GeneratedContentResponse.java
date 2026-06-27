package es.sindicato.intelligence.content.api;

import es.sindicato.intelligence.content.domain.ContentStatus;

import java.time.OffsetDateTime;

public record GeneratedContentResponse(
        Long id,
        Long eventId,
        Long analysisId,
        Long createdBy,
        String channel,
        String tone,
        String title,
        String content,
        ContentStatus status,
        OffsetDateTime generatedAt,
        OffsetDateTime approvedAt
) {
}
