package es.sindicato.intelligence.user.domain;

public interface UserAuditLogRepository {

    void record(Long userId, String actorEmail, UserAuditAction action, String details);
}
