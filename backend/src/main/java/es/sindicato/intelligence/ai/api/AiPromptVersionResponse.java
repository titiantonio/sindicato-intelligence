package es.sindicato.intelligence.ai.api;

import java.time.OffsetDateTime;

public record AiPromptVersionResponse(
        String promptKey,
        String promptName,
        String module,
        String version,
        String checksum,
        boolean active,
        OffsetDateTime createdAt
) {
}
