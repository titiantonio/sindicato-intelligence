package es.sindicato.intelligence.ai.api;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

import java.math.BigDecimal;

public record UpdateAiWorkflowSettingRequest(
        @NotBlank String providerCode,
        @NotBlank String modelName,
        @DecimalMin("0.0") @DecimalMax("2.0") BigDecimal temperature,
        @Min(1) int maxOutputTokens
) {
}
