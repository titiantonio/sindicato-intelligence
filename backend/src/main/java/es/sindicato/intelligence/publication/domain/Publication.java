package es.sindicato.intelligence.publication.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class Publication {

    private final Long id;
    private final Long contentId;
    private final String channel;
    private String externalId;
    private PublicationStatus status;
    private OffsetDateTime publishedAt;
    private String responsePayload;

    public Publication(
            Long id,
            Long contentId,
            String channel,
            String externalId,
            PublicationStatus status,
            OffsetDateTime publishedAt,
            String responsePayload
    ) {
        this.id = id;
        this.contentId = Objects.requireNonNull(contentId, "contentId is required");
        this.channel = requireText(channel, "channel");
        this.externalId = externalId;
        this.status = Objects.requireNonNull(status, "status is required");
        this.publishedAt = publishedAt;
        this.responsePayload = responsePayload;

        if (status == PublicationStatus.PUBLISHED && publishedAt == null) {
            throw new IllegalArgumentException("publishedAt is required for published publications");
        }
    }

    public static Publication pending(Long contentId, String channel) {
        return new Publication(null, contentId, channel, null, PublicationStatus.PENDING, null, null);
    }

    public void markPublished(String externalId, OffsetDateTime publishedAt, String responsePayload) {
        this.externalId = requireText(externalId, "externalId");
        this.publishedAt = Objects.requireNonNull(publishedAt, "publishedAt is required");
        this.responsePayload = responsePayload;
        this.status = PublicationStatus.PUBLISHED;
    }

    public void markFailed(String responsePayload) {
        this.externalId = null;
        this.publishedAt = null;
        this.responsePayload = responsePayload;
        this.status = PublicationStatus.FAILED;
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

    public String getResponsePayload() {
        return responsePayload;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }
}
