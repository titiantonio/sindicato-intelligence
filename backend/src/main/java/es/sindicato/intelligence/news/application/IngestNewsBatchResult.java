package es.sindicato.intelligence.news.application;

import java.util.List;

public record IngestNewsBatchResult(
        int totalReceived,
        int createdCount,
        int failedCount,
        List<IngestNewsBatchItemResult> results
) {
}
