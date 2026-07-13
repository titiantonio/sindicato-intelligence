package es.sindicato.intelligence.content.application;

import java.util.List;

public record ContentGenerationContext(
        int newsCount,
        int tracedAssociations,
        Integer averageConfidence,
        boolean hasReviewRecommendedMatches,
        List<String> matchDecisions,
        List<String> matchReasons
) {
}
