package es.sindicato.intelligence.publication.domain;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;

public class TelegramPublicationSettings {

    public static final int DEFAULT_MAX_ATTACHMENT_COUNT = 10;
    public static final long DEFAULT_MAX_ATTACHMENT_FILE_BYTES = 20_971_520L;
    public static final long DEFAULT_MAX_ATTACHMENT_TOTAL_BYTES = 52_428_800L;

    private final short id;
    private boolean enabled;
    private String baseUrl;
    private String botToken;
    private String chatId;
    private boolean disableWebPagePreview;
    private int maxAttachmentCount;
    private long maxAttachmentFileBytes;
    private long maxAttachmentTotalBytes;
    private List<TelegramPublicationDestination> destinations;
    private final OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public TelegramPublicationSettings(
            short id,
            boolean enabled,
            String baseUrl,
            String botToken,
            String chatId,
            boolean disableWebPagePreview,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(
                id,
                enabled,
                baseUrl,
                botToken,
                chatId,
                disableWebPagePreview,
                DEFAULT_MAX_ATTACHMENT_COUNT,
                DEFAULT_MAX_ATTACHMENT_FILE_BYTES,
                DEFAULT_MAX_ATTACHMENT_TOTAL_BYTES,
                List.of(),
                createdAt,
                updatedAt
        );
    }

    public TelegramPublicationSettings(
            short id,
            boolean enabled,
            String baseUrl,
            String botToken,
            String chatId,
            boolean disableWebPagePreview,
            List<TelegramPublicationDestination> destinations,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this(
                id,
                enabled,
                baseUrl,
                botToken,
                chatId,
                disableWebPagePreview,
                DEFAULT_MAX_ATTACHMENT_COUNT,
                DEFAULT_MAX_ATTACHMENT_FILE_BYTES,
                DEFAULT_MAX_ATTACHMENT_TOTAL_BYTES,
                destinations,
                createdAt,
                updatedAt
        );
    }

    public TelegramPublicationSettings(
            short id,
            boolean enabled,
            String baseUrl,
            String botToken,
            String chatId,
            boolean disableWebPagePreview,
            int maxAttachmentCount,
            long maxAttachmentFileBytes,
            long maxAttachmentTotalBytes,
            List<TelegramPublicationDestination> destinations,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        if (id != 1) {
            throw new IllegalArgumentException("telegram settings id must be 1");
        }
        this.id = id;
        this.enabled = enabled;
        this.baseUrl = requireText(baseUrl, "baseUrl is required");
        this.botToken = normalize(botToken);
        this.chatId = normalize(chatId);
        this.disableWebPagePreview = disableWebPagePreview;
        this.maxAttachmentCount = validatePositive(maxAttachmentCount, "maxAttachmentCount");
        this.maxAttachmentFileBytes = validatePositive(maxAttachmentFileBytes, "maxAttachmentFileBytes");
        this.maxAttachmentTotalBytes = validatePositive(maxAttachmentTotalBytes, "maxAttachmentTotalBytes");
        if (this.maxAttachmentTotalBytes < this.maxAttachmentFileBytes) {
            throw new IllegalArgumentException("maxAttachmentTotalBytes must be greater than or equal to maxAttachmentFileBytes");
        }
        this.destinations = List.copyOf(destinations == null ? List.of() : destinations);
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt is required");
        this.updatedAt = Objects.requireNonNull(updatedAt, "updatedAt is required");
    }

    public void update(boolean enabled, String baseUrl, String botToken, String chatId, boolean disableWebPagePreview, OffsetDateTime now) {
        update(
                enabled,
                baseUrl,
                botToken,
                chatId,
                disableWebPagePreview,
                maxAttachmentCount,
                maxAttachmentFileBytes,
                maxAttachmentTotalBytes,
                this.destinations,
                now
        );
    }

    public void update(
            boolean enabled,
            String baseUrl,
            String botToken,
            String chatId,
            boolean disableWebPagePreview,
            List<TelegramPublicationDestination> destinations,
            OffsetDateTime now
    ) {
        update(
                enabled,
                baseUrl,
                botToken,
                chatId,
                disableWebPagePreview,
                maxAttachmentCount,
                maxAttachmentFileBytes,
                maxAttachmentTotalBytes,
                destinations,
                now
        );
    }

    public void update(
            boolean enabled,
            String baseUrl,
            String botToken,
            String chatId,
            boolean disableWebPagePreview,
            int maxAttachmentCount,
            long maxAttachmentFileBytes,
            long maxAttachmentTotalBytes,
            List<TelegramPublicationDestination> destinations,
            OffsetDateTime now
    ) {
        this.enabled = enabled;
        this.baseUrl = requireText(baseUrl, "baseUrl is required");
        if (botToken != null) {
            this.botToken = normalize(botToken);
        }
        this.chatId = normalize(chatId);
        this.disableWebPagePreview = disableWebPagePreview;
        this.maxAttachmentCount = validatePositive(maxAttachmentCount, "maxAttachmentCount");
        this.maxAttachmentFileBytes = validatePositive(maxAttachmentFileBytes, "maxAttachmentFileBytes");
        this.maxAttachmentTotalBytes = validatePositive(maxAttachmentTotalBytes, "maxAttachmentTotalBytes");
        if (this.maxAttachmentTotalBytes < this.maxAttachmentFileBytes) {
            throw new IllegalArgumentException("maxAttachmentTotalBytes must be greater than or equal to maxAttachmentFileBytes");
        }
        this.destinations = List.copyOf(destinations == null ? List.of() : destinations);
        this.updatedAt = Objects.requireNonNull(now, "now is required");
    }

    public boolean isReadyToPublish() {
        return enabled && hasText(botToken) && hasText(baseUrl) && (activeDestinations().size() > 0 || hasText(chatId));
    }

    public List<TelegramPublicationDestination> activeDestinations() {
        return destinations.stream()
                .filter(TelegramPublicationDestination::isActive)
                .toList();
    }

    public List<TelegramPublicationDestination> defaultDestinations() {
        List<TelegramPublicationDestination> selected = activeDestinations().stream()
                .filter(TelegramPublicationDestination::isDefaultSelected)
                .toList();
        return selected.isEmpty() ? activeDestinations() : selected;
    }

    public short getId() {
        return id;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public String getBotToken() {
        return botToken;
    }

    public String getChatId() {
        return chatId;
    }

    public boolean isDisableWebPagePreview() {
        return disableWebPagePreview;
    }

    public int getMaxAttachmentCount() {
        return maxAttachmentCount;
    }

    public long getMaxAttachmentFileBytes() {
        return maxAttachmentFileBytes;
    }

    public long getMaxAttachmentTotalBytes() {
        return maxAttachmentTotalBytes;
    }

    public List<TelegramPublicationDestination> getDestinations() {
        return destinations;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    private String requireText(String value, String message) {
        if (!hasText(value)) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    private String normalize(String value) {
        return hasText(value) ? value.trim() : null;
    }

    private boolean hasText(String value) {
        return value != null && !value.isBlank();
    }

    private int validatePositive(int value, String fieldName) {
        if (value < 1) {
            throw new IllegalArgumentException(fieldName + " must be at least 1");
        }
        return value;
    }

    private long validatePositive(long value, String fieldName) {
        if (value < 1) {
            throw new IllegalArgumentException(fieldName + " must be at least 1");
        }
        return value;
    }
}
