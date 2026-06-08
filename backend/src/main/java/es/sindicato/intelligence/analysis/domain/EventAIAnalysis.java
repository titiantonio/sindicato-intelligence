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
        this.id = id;
        this.eventId = Objects.requireNonNull(eventId, "eventId is required");
        this.executiveSummary = requireText(executiveSummary, "executiveSummary");
        this.unionSummary = requireText(unionSummary, "unionSummary");
        this.keyPoints = List.copyOf(Objects.requireNonNull(keyPoints, "keyPoints is required"));
        this.risks = List.copyOf(Objects.requireNonNull(risks, "risks is required"));
        this.opportunities = List.copyOf(Objects.requireNonNull(opportunities, "opportunities is required"));
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
