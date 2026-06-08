package es.sindicato.intelligence.analysis.application;

import java.time.OffsetDateTime;

public record AnalysisNewsItem(
        Long id,
        String title,
        String summary,
        String content,
        OffsetDateTime publishedAt
) {
}
