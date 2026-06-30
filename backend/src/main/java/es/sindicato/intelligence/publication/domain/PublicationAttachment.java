package es.sindicato.intelligence.publication.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class PublicationAttachment {

    private final Long id;
    private final Long publicationId;
    private final String originalFilename;
    private final PublicationMediaType mediaType;
    private final String mimeType;
    private final long fileSizeBytes;
    private final String storagePath;
    private final String telegramMethod;
    private final int position;
    private final OffsetDateTime createdAt;

    public PublicationAttachment(
            Long id,
            Long publicationId,
            String originalFilename,
            PublicationMediaType mediaType,
            String mimeType,
            long fileSizeBytes,
            String storagePath,
            String telegramMethod,
            int position,
            OffsetDateTime createdAt
    ) {
        this.id = id;
        this.publicationId = Objects.requireNonNull(publicationId, "publicationId is required");
        this.originalFilename = requireText(originalFilename, "originalFilename");
        this.mediaType = Objects.requireNonNull(mediaType, "mediaType is required");
        this.mimeType = requireText(mimeType, "mimeType");
        if (fileSizeBytes <= 0) {
            throw new IllegalArgumentException("fileSizeBytes must be positive");
        }
        this.fileSizeBytes = fileSizeBytes;
        this.storagePath = requireText(storagePath, "storagePath");
        this.telegramMethod = requireText(telegramMethod, "telegramMethod");
        if (position < 0) {
            throw new IllegalArgumentException("position must be zero or positive");
        }
        this.position = position;
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
    }

    public Long getId() {
        return id;
    }

    public Long getPublicationId() {
        return publicationId;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public PublicationMediaType getMediaType() {
        return mediaType;
    }

    public String getMimeType() {
        return mimeType;
    }

    public long getFileSizeBytes() {
        return fileSizeBytes;
    }

    public String getStoragePath() {
        return storagePath;
    }

    public String getTelegramMethod() {
        return telegramMethod;
    }

    public int getPosition() {
        return position;
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
