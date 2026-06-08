package es.sindicato.intelligence.analysis.application;

import java.util.List;

public record AnalysisAIResponse(
        String executiveSummary,
        String unionSummary,
        List<String> keyPoints,
        List<String> risks,
        List<String> opportunities,
        String modelUsed
) {
}
