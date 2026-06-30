package es.sindicato.intelligence.publication.infrastructure;

import com.fasterxml.jackson.databind.JsonNode;
import es.sindicato.intelligence.publication.domain.PublicationStatus;
import es.sindicato.intelligence.publication.domain.PublicationType;
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

    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "channel", nullable = false, length = 50)
    private String channel;

    @Enumerated(EnumType.STRING)
    @Column(name = "publication_type", nullable = false, length = 50)
    private PublicationType publicationType;

    @Column(name = "title_snapshot", length = 500)
    private String titleSnapshot;

    @Column(name = "message_snapshot")
    private String messageSnapshot;

    @Column(name = "requested_by")
    private Long requestedBy;

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
            PublicationType publicationType,
            String titleSnapshot,
            String messageSnapshot,
            Long requestedBy,
            String externalId,
            PublicationStatus status,
            OffsetDateTime publishedAt,
            JsonNode responsePayload,
            OffsetDateTime scheduledAt
    ) {
        this.id = id;
        this.contentId = contentId;
        this.channel = channel;
        this.publicationType = publicationType;
        this.titleSnapshot = titleSnapshot;
        this.messageSnapshot = messageSnapshot;
        this.requestedBy = requestedBy;
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

    public PublicationType getPublicationType() {
        return publicationType;
    }

    public String getTitleSnapshot() {
        return titleSnapshot;
    }

    public String getMessageSnapshot() {
        return messageSnapshot;
    }

    public Long getRequestedBy() {
        return requestedBy;
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
