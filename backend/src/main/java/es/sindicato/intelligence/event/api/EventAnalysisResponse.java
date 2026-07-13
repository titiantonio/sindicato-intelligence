package es.sindicato.intelligence.event.api;

import java.time.OffsetDateTime;
import java.util.List;

public record EventAnalysisResponse(
        Long id,
        Long eventId,
        String executiveSummary,
        String unionSummary,
        List<String> keyPoints,
        List<String> risks,
        List<String> opportunities,
        List<String> affectedGroups,
        List<String> recommendedMonitoring,
        String analysisType,
        String generationTrigger,
        OffsetDateTime eventUpdatedAtSnapshot,
        int contextNewsCount,
        boolean contextTruncated,
        boolean outdated,
        String modelUsed,
        OffsetDateTime generatedAt
) {
}
