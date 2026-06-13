package es.sindicato.intelligence.publication.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import es.sindicato.intelligence.publication.domain.PublicationStatus;
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

@Entity
@Table(name = "publications")
public class PublicationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "content_id", nullable = false)
    private Long contentId;

    @Column(name = "channel", nullable = false, length = 50)
    private String channel;

    @Column(name = "external_id", length = 255)
    private String externalId;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_status", nullable = false, length = 50)
    private PublicationStatus status;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "scheduled_at")
    private OffsetDateTime scheduledAt;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_payload", columnDefinition = "jsonb")
    private JsonNode responsePayload;

    protected PublicationEntity() {
    }

    public PublicationEntity(
            Long id,
            Long contentId,
            String channel,
            String externalId,
            PublicationStatus status,
            OffsetDateTime publishedAt,
            JsonNode responsePayload,
            OffsetDateTime scheduledAt
    ) {
        this.id = id;
        this.contentId = contentId;
        this.channel = channel;
        this.externalId = externalId;
        this.status = status;
        this.publishedAt = publishedAt;
        this.responsePayload = responsePayload;
        this.scheduledAt = scheduledAt;
    }

    public Long getId() {
        return id;
    }

    public Long getContentId() {
        return contentId;
    }

    public String getChannel() {
        return channel;
    }

    public String getExternalId() {
        return externalId;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public JsonNode getResponsePayload() {
        return responsePayload;
    }

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }
}
