package es.sindicato.intelligence.news.api;

import es.sindicato.intelligence.news.domain.NewsStatus;

import java.time.OffsetDateTime;

public record NewsResponse(
        Long id,
        Long sourceId,
        String title,
        String url,
        String summary,
        String content,
        String hash,
        OffsetDateTime publishedAt,
        OffsetDateTime capturedAt,
        NewsStatus processingStatus,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
