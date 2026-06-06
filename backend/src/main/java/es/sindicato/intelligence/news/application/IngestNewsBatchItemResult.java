package es.sindicato.intelligence.news.application;

public record IngestNewsBatchItemResult(
        int index,
        String url,
        boolean created,
        Long newsId,
        String error
) {
}
