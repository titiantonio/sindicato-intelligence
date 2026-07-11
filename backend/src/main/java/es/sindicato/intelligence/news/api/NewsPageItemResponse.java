package es.sindicato.intelligence.news.api;

import java.time.OffsetDateTime;

public record NewsPageItemResponse(
        Long id,
        Long sourceId,
        String sourceName,
        String title,
        String url,
        String processingStatus,
        Long eventId,
        String category,
        OffsetDateTime publishedAt,
        OffsetDateTime capturedAt
) {
}
