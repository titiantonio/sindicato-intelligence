package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.news.domain.NewsStatus;

import java.time.OffsetDateTime;

public record EventNewsResponse(
        Long id,
        Long sourceId,
        String title,
        String url,
        String summary,
        NewsStatus processingStatus,
        OffsetDateTime publishedAt,
        OffsetDateTime capturedAt,
        EventNewsClassificationResponse classification
) {
}