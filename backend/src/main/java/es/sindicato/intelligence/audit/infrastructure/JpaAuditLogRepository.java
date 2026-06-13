package es.sindicato.intelligence.audit.infrastructure;

import es.sindicato.intelligence.audit.domain.AuditLogEntry;
import es.sindicato.intelligence.audit.domain.AuditLogQuery;
import es.sindicato.intelligence.audit.domain.AuditLogRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class JpaAuditLogRepository implements AuditLogRepository {

    private final EntityManager entityManager;

    public JpaAuditLogRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public void record(Long userId, String action, String entityType, Long entityId, String oldValues, String newValues) {
        entityManager.persist(new AuditLogEntity(
                null,
                userId,
                action,
                entityType,
                entityId,
                oldValues,
                newValues,
                OffsetDateTime.now()
        ));
    }

    @Override
    public List<AuditLogEntry> findEditorial(AuditLogQuery query) {
        StringBuilder jpql = new StringBuilder("SELECT log FROM AuditLogEntity log WHERE 1 = 1");
        Map<String, Object> parameters = new HashMap<>();

        if (query.action() != null && !query.action().isBlank()) {
            jpql.append(" AND log.action = :action");
            parameters.put("action", query.action());
        }
        if (query.entityType() != null && !query.entityType().isBlank()) {
            jpql.append(" AND log.entityType = :entityType");
            parameters.put("entityType", query.entityType());
        }
        if (query.entityId() != null) {
            jpql.append(" AND log.entityId = :entityId");
            parameters.put("entityId", query.entityId());
        }

        jpql.append(" ORDER BY log.createdAt DESC, log.id DESC");

        var typedQuery = entityManager.createQuery(jpql.toString(), AuditLogEntity.class)
                .setMaxResults(query.limit());
        parameters.forEach(typedQuery::setParameter);

        return typedQuery.getResultStream()
                .map(this::toEntry)
                .toList();
    }

    private AuditLogEntry toEntry(AuditLogEntity entity) {
        return new AuditLogEntry(
                entity.getId(),
                entity.getUserId(),
                entity.getAction(),
                entity.getEntityType(),
                entity.getEntityId(),
                entity.getOldValues(),
                entity.getNewValues(),
                entity.getCreatedAt()
        );
    }
}
