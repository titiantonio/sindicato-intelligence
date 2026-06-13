package es.sindicato.intelligence.audit.application;

import es.sindicato.intelligence.audit.domain.UserAuditLogQuery;
import es.sindicato.intelligence.user.infrastructure.UserAuditLogEntity;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ListUserAuditUseCase {

    private final EntityManager entityManager;

    public ListUserAuditUseCase(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Transactional(readOnly = true)
    public List<UserAuditLogEntity> execute(UserAuditLogQuery query) {
        StringBuilder jpql = new StringBuilder("SELECT log FROM UserAuditLogEntity log WHERE 1 = 1");
        Map<String, Object> parameters = new HashMap<>();

        if (query.action() != null && !query.action().isBlank()) {
            jpql.append(" AND log.action = :action");
            parameters.put("action", query.action());
        }
        if (query.userId() != null) {
            jpql.append(" AND log.userId = :userId");
            parameters.put("userId", query.userId());
        }

        jpql.append(" ORDER BY log.createdAt DESC, log.id DESC");

        var typedQuery = entityManager.createQuery(jpql.toString(), UserAuditLogEntity.class)
                .setMaxResults(query.limit());
        parameters.forEach(typedQuery::setParameter);
        return typedQuery.getResultList();
    }
}
