package es.sindicato.intelligence.publication.api;

import java.time.OffsetDateTime;
import java.util.List;

public record TelegramPublicationSettingsResponse(
        boolean enabled,
        String baseUrl,
        String chatId,
        boolean disableWebPagePreview,
        boolean botTokenConfigured,
        String botTokenPreview,
        boolean readyToPublish,
        int maxAttachmentCount,
        long maxAttachmentFileBytes,
        long maxAttachmentTotalBytes,
        OffsetDateTime updatedAt,
        List<TelegramPublicationDestinationResponse> destinations
) {
    public TelegramPublicationSettingsResponse(
            boolean enabled,
            String baseUrl,
            String chatId,
            boolean disableWebPagePreview,
            boolean botTokenConfigured,
            String botTokenPreview,
            boolean readyToPublish,
            OffsetDateTime updatedAt
    ) {
        this(
                enabled,
                baseUrl,
                chatId,
                disableWebPagePreview,
                botTokenConfigured,
                botTokenPreview,
                readyToPublish,
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_COUNT,
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_FILE_BYTES,
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_TOTAL_BYTES,
                updatedAt,
                List.of()
        );
    }
}
