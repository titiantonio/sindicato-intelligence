package es.sindicato.intelligence.news.api;

import es.sindicato.intelligence.event.api.EventNewsClassificationResponse;
import es.sindicato.intelligence.news.domain.NewsStatus;

import java.time.OffsetDateTime;

public record NewsResponse(
        Long id,
        Long sourceId,
        String sourceName,
        String title,
        String url,
        String summary,
        String content,
        String hash,
        OffsetDateTime publishedAt,
        OffsetDateTime capturedAt,
        NewsStatus processingStatus,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt,
        Long eventId,
        EventNewsClassificationResponse classification
) {
    public NewsResponse(
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
        this(
                id,
                sourceId,
                null,
                title,
                url,
                summary,
                content,
                hash,
                publishedAt,
                capturedAt,
                processingStatus,
                createdAt,
                updatedAt,
                null,
                null
        );
    }
}
