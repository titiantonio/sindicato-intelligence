package es.sindicato.intelligence.audit.domain;

public record UserAuditLogQuery(
        String action,
        Long userId,
        int limit
) {
    public UserAuditLogQuery {
        if (limit <= 0) {
            limit = 100;
        }
        if (limit > 500) {
            limit = 500;
        }
    }
}
