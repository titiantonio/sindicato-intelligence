package es.sindicato.intelligence.audit.application;

import es.sindicato.intelligence.audit.domain.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecordAuditLogUseCase {

    private final AuditLogRepository auditLogRepository;
    private final CurrentAuditUserProvider currentAuditUserProvider;

    public RecordAuditLogUseCase(
            AuditLogRepository auditLogRepository,
            CurrentAuditUserProvider currentAuditUserProvider
    ) {
        this.auditLogRepository = auditLogRepository;
        this.currentAuditUserProvider = currentAuditUserProvider;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void record(String action, String entityType, Long entityId, String oldValues, String newValues) {
        auditLogRepository.record(
                currentAuditUserProvider.currentUserId().orElse(null),
                action,
                entityType,
                entityId,
                oldValues,
                newValues
        );
    }
}
