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
    private final OffsetDateTime scheduledAt;

    public Publication(
            Long id,
            Long contentId,
            String channel,
            String externalId,
            PublicationStatus status,
            OffsetDateTime publishedAt,
            String responsePayload
    ) {
        this(id, contentId, channel, externalId, status, publishedAt, responsePayload, null);
    }

    public Publication(
            Long id,
            Long contentId,
            String channel,
            String externalId,
            PublicationStatus status,
            OffsetDateTime publishedAt,
            String responsePayload,
            OffsetDateTime scheduledAt
    ) {
        this.id = id;
        this.contentId = Objects.requireNonNull(contentId, "contentId is required");
        this.channel = requireText(channel, "channel");
        this.externalId = externalId;
        this.status = Objects.requireNonNull(status, "status is required");
        this.publishedAt = publishedAt;
        this.responsePayload = responsePayload;
        this.scheduledAt = scheduledAt;

        if (status == PublicationStatus.PUBLISHED && publishedAt == null) {
            throw new IllegalArgumentException("publishedAt is required for published publications");
        }
        if (status == PublicationStatus.SCHEDULED && scheduledAt == null) {
            throw new IllegalArgumentException("scheduledAt is required for scheduled publications");
        }
    }

    public static Publication pending(Long contentId, String channel) {
        return new Publication(null, contentId, channel, null, PublicationStatus.PENDING, null, null, null);
    }

    public static Publication scheduled(Long contentId, String channel, OffsetDateTime scheduledAt) {
        return new Publication(null, contentId, channel, null, PublicationStatus.SCHEDULED, null, null, scheduledAt);
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

    public OffsetDateTime getScheduledAt() {
        return scheduledAt;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }
}
