package es.sindicato.intelligence.source.api;

import java.time.OffsetDateTime;

public record SourceResponse(
        Long id,
        String name,
        String url,
        String type,
        Integer priority,
        Boolean active,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {
}
