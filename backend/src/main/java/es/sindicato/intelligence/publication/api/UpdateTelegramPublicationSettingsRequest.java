package es.sindicato.intelligence.publication.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UpdateTelegramPublicationSettingsRequest(
        @NotNull Boolean enabled,
        @NotBlank String baseUrl,
        String botToken,
        String chatId,
        @NotNull Boolean disableWebPagePreview
) {
}
