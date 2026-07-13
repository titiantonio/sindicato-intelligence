package es.sindicato.intelligence.content.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record GenerateContentRequest(
        @NotNull
        @Min(1)
        Long eventId,

        @Min(1)
        Long analysisId,

        String channel,
        String tone,
        String contentType,
        String length
) {
}
