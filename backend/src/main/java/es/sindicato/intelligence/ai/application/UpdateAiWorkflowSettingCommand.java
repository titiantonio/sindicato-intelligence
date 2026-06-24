package es.sindicato.intelligence.ai.application;

import java.math.BigDecimal;

public record UpdateAiWorkflowSettingCommand(
        String providerCode,
        String modelName,
        BigDecimal temperature,
        int maxOutputTokens
) {
}
