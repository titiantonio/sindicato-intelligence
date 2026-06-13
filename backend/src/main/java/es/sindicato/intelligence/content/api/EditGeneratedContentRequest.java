package es.sindicato.intelligence.content.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EditGeneratedContentRequest(
        @NotBlank
        @Size(max = 500)
        String title,

        @NotBlank
        String content,

        @NotBlank
        @Size(max = 50)
        String tone
) {
}
