package es.sindicato.intelligence.analysis.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class EventAIAnalysis {

    private final Long id;
    private final Long eventId;
    private final String executiveSummary;
    private final String unionSummary;
    private final List<String> keyPoints;
    private final List<String> risks;
    private final List<String> opportunities;
    private final List<String> affectedGroups;
    private final List<String> recommendedMonitoring;
    private final AnalysisType analysisType;
    private final AnalysisGenerationTrigger generationTrigger;
    private final OffsetDateTime eventUpdatedAtSnapshot;
    private final int contextNewsCount;
    private final boolean contextTruncated;
    private final String modelUsed;
    private final OffsetDateTime generatedAt;

    public EventAIAnalysis(
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
        this(
                id,
                eventId,
                executiveSummary,
                unionSummary,
                keyPoints,
                risks,
                opportunities,
                List.of(),
                List.of(),
                AnalysisType.STANDARD,
                AnalysisGenerationTrigger.BATCH,
                generatedAt,
                0,
                false,
                modelUsed,
                generatedAt
        );
    }

    public EventAIAnalysis(
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
        this.eventId = Objects.requireNonNull(eventId, "eventId is required");
        this.executiveSummary = requireText(executiveSummary, "executiveSummary");
        this.unionSummary = requireText(unionSummary, "unionSummary");
        this.keyPoints = List.copyOf(Objects.requireNonNull(keyPoints, "keyPoints is required"));
        this.risks = List.copyOf(Objects.requireNonNull(risks, "risks is required"));
        this.opportunities = List.copyOf(Objects.requireNonNull(opportunities, "opportunities is required"));
        this.affectedGroups = List.copyOf(Objects.requireNonNull(affectedGroups, "affectedGroups is required"));
        this.recommendedMonitoring = List.copyOf(Objects.requireNonNull(recommendedMonitoring, "recommendedMonitoring is required"));
        this.analysisType = Objects.requireNonNull(analysisType, "analysisType is required");
        this.generationTrigger = Objects.requireNonNull(generationTrigger, "generationTrigger is required");
        this.eventUpdatedAtSnapshot = Objects.requireNonNull(eventUpdatedAtSnapshot, "eventUpdatedAtSnapshot is required");
        if (contextNewsCount < 0) {
            throw new IllegalArgumentException("contextNewsCount cannot be negative");
        }
        this.contextNewsCount = contextNewsCount;
        this.contextTruncated = contextTruncated;
        this.modelUsed = requireText(modelUsed, "modelUsed");
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt is required");
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

    public boolean isOutdatedFor(OffsetDateTime eventUpdatedAt) {
        return eventUpdatedAt != null && eventUpdatedAt.isAfter(eventUpdatedAtSnapshot);
    }

    public String getModelUsed() {
        return modelUsed;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }
}
