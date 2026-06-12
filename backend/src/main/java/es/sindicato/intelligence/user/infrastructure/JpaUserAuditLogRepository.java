package es.sindicato.intelligence.user.infrastructure;

import es.sindicato.intelligence.user.domain.UserAuditAction;
import es.sindicato.intelligence.user.domain.UserAuditLogRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;

@Repository
public class JpaUserAuditLogRepository implements UserAuditLogRepository {

    private final EntityManager entityManager;

    public JpaUserAuditLogRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void record(Long userId, String actorEmail, UserAuditAction action, String details) {
        entityManager.persist(new UserAuditLogEntity(
                userId,
                actorEmail,
                action.name(),
                details,
                OffsetDateTime.now()
        ));
    }
}
