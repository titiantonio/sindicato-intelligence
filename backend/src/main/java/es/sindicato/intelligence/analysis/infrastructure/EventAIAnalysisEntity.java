package es.sindicato.intelligence.analysis.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import es.sindicato.intelligence.analysis.domain.AnalysisGenerationTrigger;
import es.sindicato.intelligence.analysis.domain.AnalysisType;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "event_ai_analysis")
public class EventAIAnalysisEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "executive_summary", nullable = false, columnDefinition = "TEXT")
    private String executiveSummary;

    @Column(name = "union_summary", nullable = false, columnDefinition = "TEXT")
    private String unionSummary;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "key_points", columnDefinition = "jsonb")
    private List<String> keyPoints;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "risks", columnDefinition = "jsonb")
    private List<String> risks;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "opportunities", columnDefinition = "jsonb")
    private List<String> opportunities;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "affected_groups", columnDefinition = "jsonb")
    private List<String> affectedGroups;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "recommended_monitoring", columnDefinition = "jsonb")
    private List<String> recommendedMonitoring;

    @Enumerated(EnumType.STRING)
    @Column(name = "analysis_type", nullable = false, length = 40)
    private AnalysisType analysisType;

    @Enumerated(EnumType.STRING)
    @Column(name = "generation_trigger", nullable = false, length = 40)
    private AnalysisGenerationTrigger generationTrigger;

    @Column(name = "event_updated_at_snapshot", nullable = false)
    private OffsetDateTime eventUpdatedAtSnapshot;

    @Column(name = "context_news_count", nullable = false)
    private int contextNewsCount;

    @Column(name = "context_truncated", nullable = false)
    private boolean contextTruncated;

    @Column(name = "model_used", nullable = false, length = 100)
    private String modelUsed;

    @Column(name = "generated_at", nullable = false)
    private OffsetDateTime generatedAt;

    protected EventAIAnalysisEntity() {
    }

    public EventAIAnalysisEntity(
            Long id,
            Long eventId,
            String executiveSummary,
            String unionSummary,
            List<String> keyPoints,
            List<String> risks,
            List<String> opportunities,
            List<String> affectedGroups,
            List<String> recommendedMonitoring,
            AnalysisType analysisType,
            AnalysisGenerationTrigger generationTrigger,
            OffsetDateTime eventUpdatedAtSnapshot,
            int contextNewsCount,
            boolean contextTruncated,
            String modelUsed,
            OffsetDateTime generatedAt
    ) {
        this.id = id;
        this.eventId = eventId;
        this.executiveSummary = executiveSummary;
        this.unionSummary = unionSummary;
        this.keyPoints = keyPoints;
        this.risks = risks;
        this.opportunities = opportunities;
        this.affectedGroups = affectedGroups;
        this.recommendedMonitoring = recommendedMonitoring;
        this.analysisType = analysisType;
        this.generationTrigger = generationTrigger;
        this.eventUpdatedAtSnapshot = eventUpdatedAtSnapshot;
        this.contextNewsCount = contextNewsCount;
        this.contextTruncated = contextTruncated;
        this.modelUsed = modelUsed;
        this.generatedAt = generatedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getExecutiveSummary() {
        return executiveSummary;
    }

    public String getUnionSummary() {
        return unionSummary;
    }

    public List<String> getKeyPoints() {
        return keyPoints;
    }

    public List<String> getRisks() {
        return risks;
    }

    public List<String> getOpportunities() {
        return opportunities;
    }

    public List<String> getAffectedGroups() {
        return affectedGroups;
    }

    public List<String> getRecommendedMonitoring() {
        return recommendedMonitoring;
    }

    public AnalysisType getAnalysisType() {
        return analysisType;
    }

    public AnalysisGenerationTrigger getGenerationTrigger() {
        return generationTrigger;
    }

    public OffsetDateTime getEventUpdatedAtSnapshot() {
        return eventUpdatedAtSnapshot;
    }

    public int getContextNewsCount() {
        return contextNewsCount;
    }

    public boolean isContextTruncated() {
        return contextTruncated;
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }
}
