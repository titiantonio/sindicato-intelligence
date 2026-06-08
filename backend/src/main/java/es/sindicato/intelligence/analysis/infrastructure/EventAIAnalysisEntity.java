package es.sindicato.intelligence.analysis.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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

    public String getModelUsed() {
        return modelUsed;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }
}
