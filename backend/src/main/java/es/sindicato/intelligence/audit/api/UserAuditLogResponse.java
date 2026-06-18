package es.sindicato.intelligence.audit.api;

import java.time.OffsetDateTime;

public record UserAuditLogResponse(
        Long id,
        Long userId,
        String userDisplayName,
        String actorEmail,
        String action,
        String details,
        OffsetDateTime createdAt
) {
}
