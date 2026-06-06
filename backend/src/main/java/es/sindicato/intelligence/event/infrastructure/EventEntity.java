package es.sindicato.intelligence.event.infrastructure;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
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
@Table(name = "events")
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 100)
    private EventCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private EventStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "importance", nullable = false, length = 50)
    private Importance importance;

    @Column(name = "first_detected_at", nullable = false)
    private OffsetDateTime firstDetectedAt;

    @Column(name = "last_updated_at", nullable = false)
    private OffsetDateTime lastUpdatedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected EventEntity() {
    }

    public EventEntity(
            Long id,
            String title,
            String description,
            EventCategory category,
            EventStatus status,
            Importance importance,
            OffsetDateTime firstDetectedAt,
            OffsetDateTime lastUpdatedAt,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.category = category;
        this.status = status;
        this.importance = importance;
        this.firstDetectedAt = firstDetectedAt;
        this.lastUpdatedAt = lastUpdatedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public EventCategory getCategory() {
        return category;
    }

    public EventStatus getStatus() {
        return status;
    }

    public Importance getImportance() {
        return importance;
    }

    public OffsetDateTime getFirstDetectedAt() {
        return firstDetectedAt;
    }

    public OffsetDateTime getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
