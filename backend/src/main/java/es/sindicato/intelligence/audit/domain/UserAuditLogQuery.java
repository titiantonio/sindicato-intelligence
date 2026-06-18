package es.sindicato.intelligence.audit.domain;

import java.time.LocalDate;

public record UserAuditLogQuery(
        String action,
        Long userId,
        LocalDate date,
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
