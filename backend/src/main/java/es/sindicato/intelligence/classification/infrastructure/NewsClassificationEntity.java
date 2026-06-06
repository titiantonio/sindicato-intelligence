package es.sindicato.intelligence.classification.infrastructure;

import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "news_classifications")
public class NewsClassificationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 100)
    private ClassificationCategory category;

    @Column(name = "subcategory", length = 100)
    private String subcategory;

    @Column(name = "relevance_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal relevanceScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "impact_level", nullable = false, length = 50)
    private ImpactLevel impactLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false, length = 50)
    private UrgencyLevel urgencyLevel;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "keywords", columnDefinition = "jsonb")
    private List<String> keywords;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "entities", columnDefinition = "jsonb")
    private List<String> entities;

    @Column(name = "classified_at", nullable = false)
    private OffsetDateTime classifiedAt;

    protected NewsClassificationEntity() {
    }

    public NewsClassificationEntity(
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
        this.newsId = newsId;
        this.category = category;
        this.subcategory = subcategory;
        this.relevanceScore = relevanceScore;
        this.impactLevel = impactLevel;
        this.urgencyLevel = urgencyLevel;
        this.keywords = keywords;
        this.entities = entities;
        this.classifiedAt = classifiedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getNewsId() {
        return newsId;
    }

    public void setNewsId(Long newsId) {
        this.newsId = newsId;
    }

    public ClassificationCategory getCategory() {
        return category;
    }

    public void setCategory(ClassificationCategory category) {
        this.category = category;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public BigDecimal getRelevanceScore() {
        return relevanceScore;
    }

    public void setRelevanceScore(BigDecimal relevanceScore) {
        this.relevanceScore = relevanceScore;
    }

    public ImpactLevel getImpactLevel() {
        return impactLevel;
    }

    public void setImpactLevel(ImpactLevel impactLevel) {
        this.impactLevel = impactLevel;
    }

    public UrgencyLevel getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(UrgencyLevel urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public List<String> getKeywords() {
        return keywords;
    }

    public void setKeywords(List<String> keywords) {
        this.keywords = keywords;
    }

    public List<String> getEntities() {
        return entities;
    }

    public void setEntities(List<String> entities) {
        this.entities = entities;
    }

    public OffsetDateTime getClassifiedAt() {
        return classifiedAt;
    }

    public void setClassifiedAt(OffsetDateTime classifiedAt) {
        this.classifiedAt = classifiedAt;
    }
}
