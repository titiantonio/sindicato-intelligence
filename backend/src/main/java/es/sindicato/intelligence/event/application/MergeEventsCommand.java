package es.sindicato.intelligence.event.application;

import java.util.List;

public record MergeEventsCommand(
        Long targetEventId,
        List<Long> sourceEventIds
) {
}
