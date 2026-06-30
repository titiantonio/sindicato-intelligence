package es.sindicato.intelligence.publication.application;

import java.util.List;

public record UpdateTelegramPublicationSettingsCommand(
        boolean enabled,
        String baseUrl,
        String botToken,
        String chatId,
        boolean disableWebPagePreview,
        List<TelegramPublicationDestinationCommand> destinations
) {
    public UpdateTelegramPublicationSettingsCommand(boolean enabled, String baseUrl, String botToken, String chatId, boolean disableWebPagePreview) {
        this(enabled, baseUrl, botToken, chatId, disableWebPagePreview, List.of());
    }
}
