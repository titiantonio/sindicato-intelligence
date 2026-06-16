package es.sindicato.intelligence.automation.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateAutomationSettingRequest(
        @NotNull Boolean enabled,
        @Min(60) int intervalSeconds,
        @Min(1) int batchSize
) {
}
