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
@Table(name = "publication_targets")
public class PublicationTargetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "publication_id", nullable = false)
    private Long publicationId;

    @Column(name = "channel", nullable = false, length = 50)
    private String channel;

    @Column(name = "destination_id")
    private Long destinationId;

    @Column(name = "destination_name", nullable = false, length = 120)
    private String destinationName;

    @Column(name = "destination_address", nullable = false, length = 100)
    private String destinationAddress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private PublicationStatus status;

    @Column(name = "external_id", length = 255)
    private String externalId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "response_payload", columnDefinition = "jsonb")
    private JsonNode responsePayload;

    @Column(name = "published_at")
    private OffsetDateTime publishedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected PublicationTargetEntity() {
    }

    public PublicationTargetEntity(Long id, Long publicationId, String channel, Long destinationId, String destinationName, String destinationAddress, PublicationStatus status, String externalId, JsonNode responsePayload, OffsetDateTime publishedAt, OffsetDateTime createdAt) {
        this.id = id;
        this.publicationId = publicationId;
        this.channel = channel;
        this.destinationId = destinationId;
        this.destinationName = destinationName;
        this.destinationAddress = destinationAddress;
        this.status = status;
        this.externalId = externalId;
        this.responsePayload = responsePayload;
        this.publishedAt = publishedAt;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public Long getPublicationId() { return publicationId; }
    public String getChannel() { return channel; }
    public Long getDestinationId() { return destinationId; }
    public String getDestinationName() { return destinationName; }
    public String getDestinationAddress() { return destinationAddress; }
    public PublicationStatus getStatus() { return status; }
    public String getExternalId() { return externalId; }
    public JsonNode getResponsePayload() { return responsePayload; }
    public OffsetDateTime getPublishedAt() { return publishedAt; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
}
