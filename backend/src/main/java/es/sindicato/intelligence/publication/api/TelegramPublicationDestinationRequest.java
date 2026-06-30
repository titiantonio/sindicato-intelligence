package es.sindicato.intelligence.publication.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TelegramPublicationDestinationRequest(
        Long id,
        @NotBlank String name,
        @NotBlank String chatId,
        @NotNull Boolean active,
        @NotNull Boolean defaultSelected
) {
}
