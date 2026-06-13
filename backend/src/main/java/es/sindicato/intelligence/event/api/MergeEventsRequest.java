package es.sindicato.intelligence.event.api;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

public record MergeEventsRequest(
        @NotNull
        Long targetEventId,

        @NotNull
        @Size(min = 1)
        List<Long> sourceEventIds
) {
}
