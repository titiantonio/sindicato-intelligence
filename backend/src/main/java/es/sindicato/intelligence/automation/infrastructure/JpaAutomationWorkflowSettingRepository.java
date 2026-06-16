package es.sindicato.intelligence.automation.infrastructure;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JpaAutomationWorkflowSettingRepository implements AutomationWorkflowSettingRepository {

    private final EntityManager entityManager;

    public JpaAutomationWorkflowSettingRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public AutomationWorkflowSetting save(AutomationWorkflowSetting setting) {
        AutomationWorkflowSettingEntity entity = toEntity(setting);
        return toDomain(entityManager.merge(entity));
    }

    @Override
    public List<AutomationWorkflowSetting> findAll() {
        return entityManager.createQuery(
                        "SELECT setting FROM AutomationWorkflowSettingEntity setting ORDER BY setting.workflowCode",
                        AutomationWorkflowSettingEntity.class
                )
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<AutomationWorkflowSetting> findByCode(AutomationWorkflowCode workflowCode) {
        return Optional.ofNullable(entityManager.find(AutomationWorkflowSettingEntity.class, workflowCode))
                .map(this::toDomain);
    }

    @Override
    public List<AutomationWorkflowSetting> findDue(OffsetDateTime now) {
        return entityManager.createQuery(
                        """
                        SELECT setting
                        FROM AutomationWorkflowSettingEntity setting
                        WHERE setting.enabled = true
                          AND setting.running = false
                          AND setting.nextRunAt <= :now
                        ORDER BY setting.nextRunAt ASC, setting.workflowCode ASC
                        """,
                        AutomationWorkflowSettingEntity.class
                )
                .setParameter("now", now)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    private AutomationWorkflowSettingEntity toEntity(AutomationWorkflowSetting setting) {
        return new AutomationWorkflowSettingEntity(
                setting.getWorkflowCode(),
                setting.isEnabled(),
                setting.getIntervalSeconds(),
                setting.getBatchSize(),
                setting.isRunning(),
                setting.getLastRunAt(),
                setting.getLastSuccessAt(),
                setting.getLastFailureAt(),
                setting.getNextRunAt(),
                setting.getLastProcessedCount(),
                setting.getLastSuccessCount(),
                setting.getLastFailedCount(),
                setting.getLastSkippedCount(),
                setting.getLastError(),
                setting.getCreatedAt(),
                setting.getUpdatedAt()
        );
    }

    private AutomationWorkflowSetting toDomain(AutomationWorkflowSettingEntity entity) {
        return new AutomationWorkflowSetting(
                entity.getWorkflowCode(),
                entity.isEnabled(),
                entity.getIntervalSeconds(),
                entity.getBatchSize(),
                entity.isRunning(),
                entity.getLastRunAt(),
                entity.getLastSuccessAt(),
                entity.getLastFailureAt(),
                entity.getNextRunAt(),
                entity.getLastProcessedCount(),
                entity.getLastSuccessCount(),
                entity.getLastFailedCount(),
                entity.getLastSkippedCount(),
                entity.getLastError(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
