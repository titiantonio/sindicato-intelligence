package es.sindicato.intelligence.publication.infrastructure;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "telegram_publication_settings")
public class TelegramPublicationSettingsEntity {

    @Id
    @Column(name = "id", nullable = false)
    private short id;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "base_url", nullable = false)
    private String baseUrl;

    @Column(name = "bot_token")
    private String botToken;

    @Column(name = "chat_id")
    private String chatId;

    @Column(name = "disable_web_page_preview", nullable = false)
    private boolean disableWebPagePreview;

    @Column(name = "max_attachment_count", nullable = false)
    private int maxAttachmentCount;

    @Column(name = "max_attachment_file_bytes", nullable = false)
    private long maxAttachmentFileBytes;

    @Column(name = "max_attachment_total_bytes", nullable = false)
    private long maxAttachmentTotalBytes;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected TelegramPublicationSettingsEntity() {
    }

    public TelegramPublicationSettingsEntity(
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
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_COUNT,
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_FILE_BYTES,
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_TOTAL_BYTES,
                createdAt,
                updatedAt
        );
    }

    public TelegramPublicationSettingsEntity(
            short id,
            boolean enabled,
            String baseUrl,
            String botToken,
            String chatId,
            boolean disableWebPagePreview,
            int maxAttachmentCount,
            long maxAttachmentFileBytes,
            long maxAttachmentTotalBytes,
            OffsetDateTime createdAt,
            OffsetDateTime updatedAt
    ) {
        this.id = id;
        this.enabled = enabled;
        this.baseUrl = baseUrl;
        this.botToken = botToken;
        this.chatId = chatId;
        this.disableWebPagePreview = disableWebPagePreview;
        this.maxAttachmentCount = maxAttachmentCount;
        this.maxAttachmentFileBytes = maxAttachmentFileBytes;
        this.maxAttachmentTotalBytes = maxAttachmentTotalBytes;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
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

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }
}
