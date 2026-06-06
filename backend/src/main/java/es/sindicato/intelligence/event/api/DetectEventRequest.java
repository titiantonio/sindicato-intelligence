package es.sindicato.intelligence.event.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record DetectEventRequest(
        @NotNull @Min(1) Long newsId
) {
}
