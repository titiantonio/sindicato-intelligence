package es.sindicato.intelligence.ai.application;

import java.time.OffsetDateTime;

public record AiProviderSettingView(
        String providerCode,
        String displayName,
        boolean enabled,
        boolean apiKeyConfigured,
        String apiKeyPreview,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
