package es.sindicato.intelligence.analysis.application;

import java.util.List;

public record AnalysisAIResponse(
        String executiveSummary,
        String unionSummary,
        List<String> keyPoints,
        List<String> risks,
        List<String> opportunities,
        List<String> affectedGroups,
        List<String> recommendedMonitoring,
        String modelUsed
) {

    public AnalysisAIResponse(
            String executiveSummary,
            String unionSummary,
            List<String> keyPoints,
            List<String> risks,
            List<String> opportunities,
            String modelUsed
    ) {
        this(executiveSummary, unionSummary, keyPoints, risks, opportunities, List.of(), List.of(), modelUsed);
    }
}
