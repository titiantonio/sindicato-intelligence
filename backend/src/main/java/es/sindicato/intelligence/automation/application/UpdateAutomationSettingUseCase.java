package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UpdateAutomationSettingUseCase {

    private final AutomationWorkflowSettingRepository repository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public UpdateAutomationSettingUseCase(AutomationWorkflowSettingRepository repository, RecordAuditLogUseCase recordAuditLogUseCase) {
        this.repository = repository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public AutomationWorkflowSetting execute(AutomationWorkflowCode workflowCode, UpdateAutomationWorkflowSettingCommand command) {
        AutomationWorkflowSetting setting = repository.findByCode(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("automation workflow setting not found: " + workflowCode));
        String oldValues = AuditDetailFormatter.automationSettingUpdated(
                setting.getWorkflowCode().name(),
                setting.isEnabled(),
                setting.getIntervalSeconds(),
                setting.getBatchSize()
        );
        setting.update(command.enabled(), command.intervalSeconds(), command.batchSize(), OffsetDateTime.now());
        AutomationWorkflowSetting savedSetting = repository.save(setting);
        recordAuditLogUseCase.record(
                "AUTOMATION_SETTING_UPDATED",
                "AUTOMATION",
                null,
                oldValues,
                AuditDetailFormatter.automationSettingUpdated(
                        savedSetting.getWorkflowCode().name(),
                        savedSetting.isEnabled(),
                        savedSetting.getIntervalSeconds(),
                        savedSetting.getBatchSize()
                )
        );
        return savedSetting;
    }
}
