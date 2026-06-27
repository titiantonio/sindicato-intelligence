package es.sindicato.intelligence.news.application;

public record NewsPageQuery(
        int page,
        int pageSize,
        String global,
        String id,
        String title,
        String source,
        String status,
        String event,
        String category,
        String publishedAt,
        String capturedAt,
        String sortColumn,
        String sortDirection
) {
}
