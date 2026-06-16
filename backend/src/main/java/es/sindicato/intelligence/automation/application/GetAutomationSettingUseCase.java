package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.springframework.stereotype.Service;

@Service
public class GetAutomationSettingUseCase {

    private final AutomationWorkflowSettingRepository repository;

    public GetAutomationSettingUseCase(AutomationWorkflowSettingRepository repository) {
        this.repository = repository;
    }

    public AutomationWorkflowSetting execute(AutomationWorkflowCode workflowCode) {
        return repository.findByCode(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("automation workflow setting not found: " + workflowCode));
    }
}
