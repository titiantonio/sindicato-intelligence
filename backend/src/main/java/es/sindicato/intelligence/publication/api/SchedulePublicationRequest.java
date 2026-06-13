package es.sindicato.intelligence.publication.api;

import jakarta.validation.constraints.NotNull;

import java.time.OffsetDateTime;

public record SchedulePublicationRequest(
        @NotNull
        OffsetDateTime scheduledAt
) {
}
