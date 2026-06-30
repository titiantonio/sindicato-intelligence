package es.sindicato.intelligence.publication.api;

import java.time.OffsetDateTime;

public record TelegramPublicationDestinationResponse(
        Long id,
        String name,
        String chatId,
        boolean active,
        boolean defaultSelected,
        OffsetDateTime updatedAt
) {
}
