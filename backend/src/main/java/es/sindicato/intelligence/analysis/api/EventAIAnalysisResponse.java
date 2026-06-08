package es.sindicato.intelligence.analysis.api;

import java.time.OffsetDateTime;
import java.util.List;

public record EventAIAnalysisResponse(
        Long id,
        Long eventId,
        String executiveSummary,
        String unionSummary,
        List<String> keyPoints,
        List<String> risks,
        List<String> opportunities,
        String modelUsed,
        OffsetDateTime generatedAt
) {
}
