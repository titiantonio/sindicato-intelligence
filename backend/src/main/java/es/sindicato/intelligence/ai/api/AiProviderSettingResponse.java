package es.sindicato.intelligence.ai.api;

import java.time.OffsetDateTime;

public record AiProviderSettingResponse(
        String providerCode,
        String displayName,
        boolean enabled,
        boolean apiKeyConfigured,
        String apiKeyPreview,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
