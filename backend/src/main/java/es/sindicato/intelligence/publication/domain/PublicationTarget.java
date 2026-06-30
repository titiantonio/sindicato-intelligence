package es.sindicato.intelligence.publication.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class PublicationTarget {

    private final Long id;
    private final Long publicationId;
    private final String channel;
    private final Long destinationId;
    private final String destinationName;
    private final String destinationAddress;
    private PublicationStatus status;
    private String externalId;
    private String responsePayload;
    private OffsetDateTime publishedAt;
    private final OffsetDateTime createdAt;

    public PublicationTarget(
            Long id,
            Long publicationId,
            String channel,
            Long destinationId,
            String destinationName,
            String destinationAddress,
            PublicationStatus status,
            String externalId,
            String responsePayload,
            OffsetDateTime publishedAt,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.publicationId = Objects.requireNonNull(publicationId, "publicationId is required");
        this.channel = requireText(channel, "channel");
        this.destinationId = destinationId;
        this.destinationName = requireText(destinationName, "destinationName");
        this.destinationAddress = requireText(destinationAddress, "destinationAddress");
        this.status = Objects.requireNonNull(status, "status is required");
        this.externalId = externalId;
        this.responsePayload = responsePayload;
        this.publishedAt = publishedAt;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public static PublicationTarget pending(Long publicationId, String channel, TelegramPublicationDestination destination, OffsetDateTime now) {
        return new PublicationTarget(
                null,
                publicationId,
                channel,
                destination.getId(),
                destination.getName(),
                destination.getChatId(),
                PublicationStatus.PENDING,
                null,
                null,
                null,
                now
        );
    }

    public void markPublished(String externalId, String responsePayload, OffsetDateTime publishedAt) {
        this.externalId = requireText(externalId, "externalId");
        this.responsePayload = responsePayload;
        this.publishedAt = Objects.requireNonNull(publishedAt, "publishedAt is required");
        this.status = PublicationStatus.PUBLISHED;
    }

    public void markFailed(String responsePayload) {
        this.externalId = null;
        this.responsePayload = responsePayload;
        this.publishedAt = null;
        this.status = PublicationStatus.FAILED;
    }

    public Long getId() {
        return id;
    }

    public Long getPublicationId() {
        return publicationId;
    }

    public String getChannel() {
        return channel;
    }

    public Long getDestinationId() {
        return destinationId;
    }

    public String getDestinationName() {
        return destinationName;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public PublicationStatus getStatus() {
        return status;
    }

    public String getExternalId() {
        return externalId;
    }

    public String getResponsePayload() {
        return responsePayload;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }
        return value.trim();
    }
}
