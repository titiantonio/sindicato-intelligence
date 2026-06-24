package es.sindicato.intelligence.ai.api;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AiWorkflowSettingResponse(
        String workflowCode,
        String providerCode,
        String providerName,
        String modelName,
        BigDecimal temperature,
        int maxOutputTokens,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
