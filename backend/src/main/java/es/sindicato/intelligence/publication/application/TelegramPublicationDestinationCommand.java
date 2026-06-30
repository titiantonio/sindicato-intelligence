package es.sindicato.intelligence.publication.application;

public record TelegramPublicationDestinationCommand(
        Long id,
        String name,
        String chatId,
        boolean active,
        boolean defaultSelected
) {
}
