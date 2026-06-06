package es.sindicato.intelligence.news.application;

import java.time.OffsetDateTime;

public record CreateNewsCommand(
        Long sourceId,
        String title,
        String url,
        String summary,
        String content,
        OffsetDateTime publishedAt
) {
}
