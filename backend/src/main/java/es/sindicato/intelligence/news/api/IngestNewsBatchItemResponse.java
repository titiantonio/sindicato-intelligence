package es.sindicato.intelligence.news.api;

public record IngestNewsBatchItemResponse(
        int index,
        String url,
        boolean created,
        Long newsId,
        String error
) {
}
