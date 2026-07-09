package es.sindicato.intelligence.publication.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record UpdateTelegramPublicationSettingsRequest(
        @NotNull Boolean enabled,
        @NotBlank String baseUrl,
        String botToken,
        String chatId,
        @NotNull Boolean disableWebPagePreview,
        @Min(1) Integer maxAttachmentCount,
        @Min(1) Long maxAttachmentFileBytes,
        @Min(1) Long maxAttachmentTotalBytes,
        List<TelegramPublicationDestinationRequest> destinations
) {
}
