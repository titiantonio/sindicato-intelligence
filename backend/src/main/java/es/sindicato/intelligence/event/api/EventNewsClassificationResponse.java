package es.sindicato.intelligence.event.api;

import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

public record EventNewsClassificationResponse(
        Long id,
        Long newsId,
        ClassificationCategory category,
        String subcategory,
        BigDecimal relevanceScore,
        ImpactLevel impactLevel,
        UrgencyLevel urgencyLevel,
        List<String> keywords,
        List<String> entities,
        OffsetDateTime classifiedAt
) {
}