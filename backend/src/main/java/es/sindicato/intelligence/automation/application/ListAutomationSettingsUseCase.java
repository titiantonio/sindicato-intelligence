package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ListAutomationSettingsUseCase {

    private final AutomationWorkflowSettingRepository repository;

    public ListAutomationSettingsUseCase(AutomationWorkflowSettingRepository repository) {
        this.repository = repository;
    }

    public List<AutomationWorkflowSetting> execute() {
        return repository.findAll();
    }
}
