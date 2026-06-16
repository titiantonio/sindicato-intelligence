package es.sindicato.intelligence.publication.application;

public record UpdateTelegramPublicationSettingsCommand(
        boolean enabled,
        String baseUrl,
        String botToken,
        String chatId,
        boolean disableWebPagePreview
) {
}
