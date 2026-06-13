package es.sindicato.intelligence.audit.domain;

public record AuditLogQuery(
        String action,
        String entityType,
        Long entityId,
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
