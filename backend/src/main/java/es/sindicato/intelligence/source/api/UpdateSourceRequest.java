package es.sindicato.intelligence.source.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.URL;

public record UpdateSourceRequest(
        @NotBlank
        @Size(max = 255)
        String name,

        @NotBlank
        @URL
        String url,

        @NotBlank
        @Size(max = 50)
        String type,

        @NotNull
        @Min(0)
        Integer priority,

        @NotNull
        Boolean active
) {
}
