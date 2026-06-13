package es.sindicato.intelligence.audit.domain;

import java.util.List;

public interface AuditLogRepository {

    void record(Long userId, String action, String entityType, Long entityId, String oldValues, String newValues);

    List<AuditLogEntry> findEditorial(AuditLogQuery query);
}
