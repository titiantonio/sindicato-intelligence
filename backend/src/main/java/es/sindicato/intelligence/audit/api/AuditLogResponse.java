package es.sindicato.intelligence.audit.api;

import java.time.OffsetDateTime;

public record AuditLogResponse(
        Long id,
        Long userId,
        String action,
        String entityType,
        Long entityId,
        String oldValues,
        String newValues,
        OffsetDateTime createdAt
) {
}
