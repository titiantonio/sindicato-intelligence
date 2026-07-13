package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.ContentType;
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

import java.time.OffsetDateTime;
import java.util.Map;

@Entity
@Table(name = "generated_content")
public class GeneratedContentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "event_id", nullable = false)
    private Long eventId;

    @Column(name = "analysis_id")
    private Long analysisId;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @Column(name = "channel", nullable = false, length = 50)
    private String channel;

    @Column(name = "tone", nullable = false, length = 50)
    private String tone;

    @Enumerated(EnumType.STRING)
    @Column(name = "content_type", nullable = false, length = 40)
    private ContentType contentType;

    @Column(name = "length", nullable = false, length = 40)
    private String length;

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

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "generation_metadata", nullable = false, columnDefinition = "jsonb")
    private Map<String, Object> generationMetadata;

    protected GeneratedContentEntity() {
    }

    public GeneratedContentEntity(
            Long id,
            Long eventId,
            Long analysisId,
            Long createdBy,
            String channel,
            String tone,
            ContentType contentType,
            String length,
            String title,
            String content,
            ContentStatus status,
            OffsetDateTime generatedAt,
            OffsetDateTime approvedAt,
            Map<String, Object> generationMetadata
    ) {
        this.id = id;
        this.eventId = eventId;
        this.analysisId = analysisId;
        this.createdBy = createdBy;
        this.channel = channel;
        this.tone = tone;
        this.contentType = contentType;
        this.length = length;
        this.title = title;
        this.content = content;
        this.status = status;
        this.generatedAt = generatedAt;
        this.approvedAt = approvedAt;
        this.generationMetadata = generationMetadata;
    }

    public Long getId() {
        return id;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getAnalysisId() {
        return analysisId;
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

    public ContentType getContentType() {
        return contentType;
    }

    public String getLength() {
        return length;
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

    public Map<String, Object> getGenerationMetadata() {
        return generationMetadata;
    }
}
