package es.sindicato.intelligence.news.api;

import java.util.List;

public record IngestNewsBatchResponse(
        int totalReceived,
        int createdCount,
        int failedCount,
        List<IngestNewsBatchItemResponse> results
) {
}
