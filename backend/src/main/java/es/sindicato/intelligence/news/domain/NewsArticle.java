package es.sindicato.intelligence.news.domain;

import java.time.OffsetDateTime;
import java.util.Objects;

public class NewsArticle {

    private final Long id;
    private final Long sourceId;
    private final String title;
    private final String url;
    private final String summary;
    private final String content;
    private final String hash;
    private final OffsetDateTime publishedAt;
    private final OffsetDateTime capturedAt;
    private NewsStatus processingStatus;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public NewsArticle(
            Long id,
            Long sourceId,
            String title,
            String url,
            String summary,
            String content,
            String hash,
            OffsetDateTime publishedAt,
            OffsetDateTime capturedAt,
            NewsStatus processingStatus,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.sourceId = Objects.requireNonNull(sourceId, "sourceId is required");
        this.title = requireText(title, "title");
        this.url = requireText(url, "url");
        this.summary = summary;
        this.content = content;
        this.hash = requireHash(hash);
        this.publishedAt = publishedAt;
        this.capturedAt = Objects.requireNonNull(capturedAt, "capturedAt is required");
        this.processingStatus = Objects.requireNonNull(processingStatus, "processingStatus is required");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before createdAt");
        }
    }

    public void markClassified() {
        changeStatus(NewsStatus.CLASSIFIED, OffsetDateTime.now());
    }

    public void markCaptured() {
        changeStatus(NewsStatus.CAPTURED, OffsetDateTime.now());
    }

    public void markDiscarded() {
        changeStatus(NewsStatus.DISCARDED, OffsetDateTime.now());
    }

    public void markEventMatched() {
        changeStatus(NewsStatus.EVENT_MATCHED, OffsetDateTime.now());
    }

    public void archive() {
        changeStatus(NewsStatus.ARCHIVED, OffsetDateTime.now());
    }

    void changeStatus(NewsStatus status, OffsetDateTime updatedAt) {
        this.processingStatus = Objects.requireNonNull(status, "status is required");
        updateTimestamp(updatedAt);
    }

    public Long getId() {
        return id;
    }

    public Long getSourceId() {
        return sourceId;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getSummary() {
        return summary;
    }

    public String getContent() {
        return content;
    }

    public String getHash() {
        return hash;
    }

    public OffsetDateTime getPublishedAt() {
        return publishedAt;
    }

    public OffsetDateTime getCapturedAt() {
        return capturedAt;
    }

    public NewsStatus getProcessingStatus() {
        return processingStatus;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private void updateTimestamp(OffsetDateTime updatedAt) {
        Objects.requireNonNull(updatedAt, "updatedAt is required");

        if (updatedAt.isBefore(createdAt)) {
            throw new IllegalArgumentException("updatedAt cannot be before createdAt");
        }

        this.updatedAt = updatedAt;
    }

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }

    private static String requireHash(String hash) {
        String value = requireText(hash, "hash");
        if (value.length() != 64) {
            throw new IllegalArgumentException("hash must have 64 characters");
        }
        return value;
    }
}
