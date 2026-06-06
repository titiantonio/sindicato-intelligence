package es.sindicato.intelligence.classification.application;

import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;

import java.math.BigDecimal;
import java.util.List;

public record ClassificationAIResponse(
        ClassificationCategory category,
        String subcategory,
        BigDecimal relevance,
        ImpactLevel impact,
        UrgencyLevel urgency,
        List<String> keywords,
        List<String> entities,
        String summary
) {
}
