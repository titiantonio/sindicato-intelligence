package es.sindicato.intelligence.analysis.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateAnalysisRequest(
        @NotNull
        @Min(1)
        Long eventId
) {
}
