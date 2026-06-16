package es.sindicato.intelligence.publication.api;

import java.time.OffsetDateTime;

public record TelegramPublicationSettingsResponse(
        boolean enabled,
        String baseUrl,
        String chatId,
        boolean disableWebPagePreview,
        boolean botTokenConfigured,
        String botTokenPreview,
        boolean readyToPublish,
        OffsetDateTime updatedAt
) {
}
