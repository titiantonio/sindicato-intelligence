package es.sindicato.intelligence.news.api;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.validator.constraints.URL;

import java.time.OffsetDateTime;

public record CreateNewsRequest(
        @NotNull
        @Min(1)
        Long sourceId,

        @NotBlank
        String title,

        @NotBlank
        @URL
        String url,

        String summary,

        String content,

        OffsetDateTime publishedAt
) {
}
