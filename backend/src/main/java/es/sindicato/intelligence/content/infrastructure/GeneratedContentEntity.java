package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.domain.ContentStatus;
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
@Table(name = "generated_content")
public class GeneratedContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "channel", nullable = false, length = 50)
    private String channel;

    @Column(name = "tone", nullable = false, length = 50)
    private String tone;

    @Column(name = "title", nullable = false, length = 500)
    private String title;

    @Column(name = "content", nullable = false, columnDefinition = "TEXT")
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private ContentStatus status;

    @Column(name = "generated_at", nullable = false)
    private OffsetDateTime generatedAt;

    @Column(name = "approved_at")
    private OffsetDateTime approvedAt;

    protected GeneratedContentEntity() {
    }

    public GeneratedContentEntity(
            Long id,
            Long eventId,
            Long createdBy,
            String channel,
            String tone,
            String title,
            String content,
            ContentStatus status,
            OffsetDateTime generatedAt,
            OffsetDateTime approvedAt
    ) {
        this.id = id;
        this.eventId = eventId;
        this.createdBy = createdBy;
        this.channel = channel;
        this.tone = tone;
        this.title = title;
        this.content = content;
        this.status = status;
        this.generatedAt = generatedAt;
        this.approvedAt = approvedAt;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public String getChannel() {
        return channel;
    }

    public String getTone() {
        return tone;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public ContentStatus getStatus() {
        return status;
    }

    public OffsetDateTime getGeneratedAt() {
        return generatedAt;
    }

    public OffsetDateTime getApprovedAt() {
        return approvedAt;
    }
}
