package es.sindicato.intelligence.event.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected EventNewsEntity() {
    }

    public EventNewsEntity(Long id, Long eventId, Long newsId, Integer confidenceScore, OffsetDateTime createdAt) {
        this.id = id;
        this.eventId = eventId;
        this.newsId = newsId;
        this.confidenceScore = confidenceScore;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }
}
