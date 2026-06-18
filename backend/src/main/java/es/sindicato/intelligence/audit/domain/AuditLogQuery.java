package es.sindicato.intelligence.audit.domain;

import java.time.LocalDate;

public record AuditLogQuery(
        String action,
        String entityType,
        Long entityId,
        LocalDate date,
        int limit
) {
    public AuditLogQuery {
        if (limit <= 0) {
            limit = 100;
        }
        if (limit > 500) {
            limit = 500;
        }
    }
}
