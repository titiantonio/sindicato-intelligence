package es.sindicato.intelligence.audit.application;

import es.sindicato.intelligence.audit.domain.AuditLogEntry;
import es.sindicato.intelligence.audit.domain.AuditLogQuery;
import es.sindicato.intelligence.audit.domain.AuditLogRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListEditorialAuditUseCase {

    private final AuditLogRepository auditLogRepository;

    public ListEditorialAuditUseCase(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    @Transactional(readOnly = true)
    public List<AuditLogEntry> execute(AuditLogQuery query) {
        return auditLogRepository.findEditorial(query);
    }
}
