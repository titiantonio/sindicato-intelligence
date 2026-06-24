package es.sindicato.intelligence.ai.application;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record AiWorkflowSettingView(
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
