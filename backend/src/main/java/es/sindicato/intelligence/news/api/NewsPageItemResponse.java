package es.sindicato.intelligence.news.api;

import java.time.OffsetDateTime;

public record NewsPageItemResponse(
        Long id,
        Long sourceId,
        String title,
        String processingStatus,
        Long eventId,
        String category,
        OffsetDateTime publishedAt,
        OffsetDateTime capturedAt
) {
}
