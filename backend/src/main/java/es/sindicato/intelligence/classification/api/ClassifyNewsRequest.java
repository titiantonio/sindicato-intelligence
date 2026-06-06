package es.sindicato.intelligence.classification.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record ClassifyNewsRequest(
        @NotNull
        @Min(1)
        Long newsId
) {
}
