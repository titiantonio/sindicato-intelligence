package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Service
public class UpdateAutomationSettingUseCase {

    private final AutomationWorkflowSettingRepository repository;

    public UpdateAutomationSettingUseCase(AutomationWorkflowSettingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public AutomationWorkflowSetting execute(AutomationWorkflowCode workflowCode, UpdateAutomationWorkflowSettingCommand command) {
        AutomationWorkflowSetting setting = repository.findByCode(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("automation workflow setting not found: " + workflowCode));
        setting.update(command.enabled(), command.intervalSeconds(), command.batchSize(), OffsetDateTime.now());
        return repository.save(setting);
    }
}
