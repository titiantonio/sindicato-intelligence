package es.sindicato.intelligence.analysis.application;

import java.time.OffsetDateTime;

public record AnalysisNewsItem(
        Long id,
        String sourceName,
        Integer sourcePriority,
        String title,
        String url,
        String summary,
        String content,
        OffsetDateTime publishedAt
) {

    public AnalysisNewsItem(
            Long id,
            String title,
            String summary,
            String content,
            OffsetDateTime publishedAt
    ) {
        this(id, null, null, title, null, summary, content, publishedAt);
    }
}
