package es.sindicato.intelligence.ai.api;

import jakarta.validation.constraints.NotNull;

public record UpdateAiProviderSettingRequest(
        @NotNull Boolean enabled,
        String apiKey,
        Boolean clearApiKey
) {
}
