package es.sindicato.intelligence.news.application;

import java.time.OffsetDateTime;

public record NewsPageItem(
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
