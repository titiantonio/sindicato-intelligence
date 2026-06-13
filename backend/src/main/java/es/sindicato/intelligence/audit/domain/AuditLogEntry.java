package es.sindicato.intelligence.audit.domain;

import java.time.OffsetDateTime;

public record AuditLogEntry(
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
