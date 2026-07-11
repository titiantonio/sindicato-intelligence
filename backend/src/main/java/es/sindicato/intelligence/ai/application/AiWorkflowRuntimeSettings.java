package es.sindicato.intelligence.ai.application;

import java.math.BigDecimal;

public record AiWorkflowRuntimeSettings(
        String workflowCode,
        String providerCode,
        String modelName,
        BigDecimal temperature,
        int maxOutputTokens,
        int cooldownSeconds,
        String apiKey
) {
}
