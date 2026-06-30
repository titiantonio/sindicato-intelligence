package es.sindicato.intelligence.publication.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class Publication {

    private final Long id;
    private final Long contentId;
    private final String channel;
    private final PublicationType publicationType;
    private final String titleSnapshot;
    private final String messageSnapshot;
    private final Long requestedBy;
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
        this(id, contentId, channel, PublicationType.GENERATED_CONTENT, null, null, null, externalId, status, publishedAt, responsePayload, null);
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
        this(id, contentId, channel, PublicationType.GENERATED_CONTENT, null, null, null, externalId, status, publishedAt, responsePayload, scheduledAt);
    }

    public Publication(
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
            String responsePayload,
            OffsetDateTime scheduledAt
    ) {
        this.id = id;
        this.contentId = contentId;
        this.channel = requireText(channel, "channel");
        this.publicationType = Objects.requireNonNull(publicationType, "publicationType is required");
        this.titleSnapshot = normalize(titleSnapshot);
        this.messageSnapshot = normalize(messageSnapshot);
        this.requestedBy = requestedBy;
        this.externalId = externalId;
        this.status = Objects.requireNonNull(status, "status is required");
        this.publishedAt = publishedAt;
        this.responsePayload = responsePayload;
        this.scheduledAt = scheduledAt;

        if (publicationType == PublicationType.GENERATED_CONTENT && contentId == null) {
            throw new IllegalArgumentException("contentId is required for generated content publications");
        }
        if (publicationType == PublicationType.MANUAL_MESSAGE && contentId != null) {
            throw new IllegalArgumentException("contentId must be null for manual publications");
        }
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

    public static Publication manual(String channel, String title, String message, Long requestedBy) {
        return new Publication(null, null, channel, PublicationType.MANUAL_MESSAGE, title, message, requestedBy, null, PublicationStatus.PENDING, null, null, null);
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

        return value.trim();
    }

    private static String normalize(String value) {
        return value == null || value.isBlank() ? null : value.trim();
    }
}
