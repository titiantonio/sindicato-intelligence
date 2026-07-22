package es.sindicato.intelligence.publication.application;

import java.util.List;

public record UpdateTelegramPublicationSettingsCommand(
        boolean enabled,
        String baseUrl,
        String botToken,
        String chatId,
        boolean disableWebPagePreview,
        int maxAttachmentCount,
        long maxAttachmentFileBytes,
        long maxAttachmentTotalBytes,
        List<TelegramPublicationDestinationCommand> destinations,
        boolean clearBotToken
) {
    public UpdateTelegramPublicationSettingsCommand(
            boolean enabled,
            String baseUrl,
            String botToken,
            String chatId,
            boolean disableWebPagePreview,
            int maxAttachmentCount,
            long maxAttachmentFileBytes,
            long maxAttachmentTotalBytes,
            List<TelegramPublicationDestinationCommand> destinations
    ) {
        this(enabled, baseUrl, botToken, chatId, disableWebPagePreview, maxAttachmentCount, maxAttachmentFileBytes, maxAttachmentTotalBytes, destinations, false);
    }

    public UpdateTelegramPublicationSettingsCommand(boolean enabled, String baseUrl, String botToken, String chatId, boolean disableWebPagePreview) {
        this(
                enabled,
                baseUrl,
                botToken,
                chatId,
                disableWebPagePreview,
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_COUNT,
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_FILE_BYTES,
                es.sindicato.intelligence.publication.domain.TelegramPublicationSettings.DEFAULT_MAX_ATTACHMENT_TOTAL_BYTES,
                List.of(),
                false
        );
    }
}
