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
        this(enabled, baseUrl, chatId, disableWebPagePreview, botTokenConfigured, botTokenPreview, readyToPublish, updatedAt, List.of());
    }
}
