package es.sindicato.intelligence.publication.application;

import java.time.OffsetDateTime;

public record SchedulePublicationCommand(
        Long contentId,
        OffsetDateTime scheduledAt
) {
}
