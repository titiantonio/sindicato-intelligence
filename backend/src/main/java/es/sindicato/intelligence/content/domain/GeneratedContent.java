package es.sindicato.intelligence.content.domain;

import java.time.OffsetDateTime;
import java.util.Map;
import java.util.Objects;

public class GeneratedContent {

    private final Long id;
    private final Long eventId;
    private final Long analysisId;
    private final Long createdBy;
    private final String channel;
    private String tone;
    private final ContentType contentType;
    private final String length;
    private String title;
    private String content;
    private ContentStatus status;
    private final OffsetDateTime generatedAt;
    private OffsetDateTime approvedAt;
    private final Map<String, Object> generationMetadata;

    public GeneratedContent(
            Long id,
            Long eventId,
            Long createdBy,
            String channel,
            String tone,
            String title,
            String content,
            ContentStatus status,
            OffsetDateTime generatedAt,
            OffsetDateTime approvedAt
    ) {
        this(
                id,
                eventId,
                null,
                createdBy,
                channel,
                tone,
                ContentType.TELEGRAM_POST,
                "STANDARD",
                title,
                content,
                status,
                generatedAt,
                approvedAt,
                Map.of()
        );
    }

    public GeneratedContent(
            Long id,
            Long eventId,
            Long analysisId,
            Long createdBy,
            String channel,
            String tone,
            String title,
            String content,
            ContentStatus status,
            OffsetDateTime generatedAt,
            OffsetDateTime approvedAt
    ) {
        this(
                id,
                eventId,
                analysisId,
                createdBy,
                channel,
                tone,
                ContentType.TELEGRAM_POST,
                "STANDARD",
                title,
                content,
                status,
                generatedAt,
                approvedAt,
                Map.of()
        );
    }

    public GeneratedContent(
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
        this.eventId = Objects.requireNonNull(eventId, "eventId is required");
        this.analysisId = analysisId;
        this.createdBy = Objects.requireNonNull(createdBy, "createdBy is required");
        this.channel = requireText(channel, "channel");
        this.tone = requireText(tone, "tone");
        this.contentType = Objects.requireNonNull(contentType, "contentType is required");
        this.length = requireText(length, "length");
        this.title = requireText(title, "title");
        this.content = requireText(content, "content");
        this.status = Objects.requireNonNull(status, "status is required");
        this.generatedAt = Objects.requireNonNull(generatedAt, "generatedAt is required");
        this.approvedAt = approvedAt;
        this.generationMetadata = Map.copyOf(Objects.requireNonNull(generationMetadata, "generationMetadata is required"));

        if (approvedAt != null && approvedAt.isBefore(generatedAt)) {
            throw new IllegalArgumentException("approvedAt cannot be before generatedAt");
        }
    }

    public void markPendingReview() {
        this.status = ContentStatus.PENDING_REVIEW;
        this.approvedAt = null;
    }

    public void edit(String title, String content, String tone) {
        if (status == ContentStatus.PUBLISHED) {
            throw new IllegalStateException("published content cannot be edited");
        }

        this.title = requireText(title, "title");
        this.content = requireText(content, "content");
        this.tone = requireText(tone, "tone");
        markPendingReview();
    }

    public void approve(OffsetDateTime approvedAt) {
        Objects.requireNonNull(approvedAt, "approvedAt is required");
        if (approvedAt.isBefore(generatedAt)) {
            throw new IllegalArgumentException("approvedAt cannot be before generatedAt");
        }

        this.status = ContentStatus.APPROVED;
        this.approvedAt = approvedAt;
    }

    public void reject() {
        this.status = ContentStatus.REJECTED;
        this.approvedAt = null;
    }

    public void markPublished() {
        if (status != ContentStatus.APPROVED) {
            throw new IllegalStateException("only approved content can be published");
        }

        this.status = ContentStatus.PUBLISHED;
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

    private static String requireText(String value, String fieldName) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(fieldName + " is required");
        }

        return value;
    }
}
