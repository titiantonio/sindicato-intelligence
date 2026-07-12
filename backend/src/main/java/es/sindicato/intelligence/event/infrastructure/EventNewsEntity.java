package es.sindicato.intelligence.event.infrastructure;

import es.sindicato.intelligence.event.domain.EventMatchDecision;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "event_news")
public class EventNewsEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "news_id", nullable = false)
    private Long newsId;

    @Column(name = "confidence_score")
    private Integer confidenceScore;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_decision", length = 80)
    private EventMatchDecision matchDecision;

    @Column(name = "match_reason", columnDefinition = "TEXT")
    private String matchReason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected EventNewsEntity() {
    }

    public EventNewsEntity(Long id, Long eventId, Long newsId, Integer confidenceScore, OffsetDateTime createdAt) {
        this(id, eventId, newsId, confidenceScore, null, null, createdAt);
    }

    public EventNewsEntity(Long id, Long eventId, Long newsId, Integer confidenceScore, EventMatchDecision matchDecision, String matchReason, OffsetDateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.newsId = newsId;
        this.confidenceScore = confidenceScore;
        this.matchDecision = matchDecision;
        this.matchReason = matchReason;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getNewsId() {
        return newsId;
    }

    public Integer getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public EventMatchDecision getMatchDecision() {
        return matchDecision;
    }

    public void setMatchDecision(EventMatchDecision matchDecision) {
        this.matchDecision = matchDecision;
    }

    public String getMatchReason() {
        return matchReason;
    }

    public void setMatchReason(String matchReason) {
        this.matchReason = matchReason;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
