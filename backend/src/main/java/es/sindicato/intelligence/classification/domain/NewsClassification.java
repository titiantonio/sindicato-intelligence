package es.sindicato.intelligence.classification.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class NewsClassification {

    private final Long id;
    private final Long newsId;
    private final ClassificationCategory category;
    private final String subcategory;
    private final BigDecimal relevanceScore;
    private final ImpactLevel impactLevel;
    private final UrgencyLevel urgencyLevel;
    private final List<String> keywords;
    private final List<String> entities;
    private final OffsetDateTime classifiedAt;

    public NewsClassification(
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
        this.id = id;
        this.newsId = Objects.requireNonNull(newsId, "newsId is required");
        this.category = Objects.requireNonNull(category, "category is required");
        this.subcategory = subcategory;
        this.relevanceScore = requireRelevanceScore(relevanceScore);
        this.impactLevel = Objects.requireNonNull(impactLevel, "impactLevel is required");
        this.urgencyLevel = Objects.requireNonNull(urgencyLevel, "urgencyLevel is required");
        this.keywords = List.copyOf(Objects.requireNonNullElse(keywords, List.of()));
        this.entities = List.copyOf(Objects.requireNonNullElse(entities, List.of()));
        this.classifiedAt = Objects.requireNonNull(classifiedAt, "classifiedAt is required");
    }

    public Long getId() {
        return id;
    }

    public Long getNewsId() {
        return newsId;
    }

    public ClassificationCategory getCategory() {
        return category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public BigDecimal getRelevanceScore() {
        return relevanceScore;
    }

    public ImpactLevel getImpactLevel() {
        return impactLevel;
    }

    public UrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public List<String> getEntities() {
        return entities;
    }

    public OffsetDateTime getClassifiedAt() {
        return classifiedAt;
    }

    public boolean isDiscardableForEventDetection() {
        return category == ClassificationCategory.OTROS
                && relevanceScore.compareTo(BigDecimal.ZERO) == 0
                && isDiscardSubcategory(subcategory);
    }

    private boolean isDiscardSubcategory(String subcategory) {
        if (subcategory == null) {
            return false;
        }

        String normalized = subcategory.trim();
        return "FUERA_DE_AMBITO".equalsIgnoreCase(normalized)
                || "INFORMACION_INSUFICIENTE".equalsIgnoreCase(normalized);
    }

    private BigDecimal requireRelevanceScore(BigDecimal relevanceScore) {
        Objects.requireNonNull(relevanceScore, "relevanceScore is required");

        if (relevanceScore.compareTo(BigDecimal.ZERO) < 0 || relevanceScore.compareTo(BigDecimal.valueOf(100)) > 0) {
            throw new IllegalArgumentException("relevanceScore must be between 0 and 100");
        }

        return relevanceScore;
    }
}
