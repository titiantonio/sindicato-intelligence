package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetAutomationOverviewUseCase {

    private final AutomationWorkflowSettingRepository repository;

    public GetAutomationOverviewUseCase(AutomationWorkflowSettingRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public AutomationOverview execute() {
        List<AutomationWorkflowSetting> workflows = repository.findAll();
        long enabled = workflows.stream().filter(AutomationWorkflowSetting::isEnabled).count();
        long failed = workflows.stream().filter(setting -> setting.getLastFailedCount() > 0 || setting.getLastError() != null).count();
        long running = workflows.stream().filter(AutomationWorkflowSetting::isRunning).count();
        return new AutomationOverview(
                "WF01_CAPTURE_NEWS",
                "WF-01-Capture-News",
                "EXTERNAL_N8N",
                workflows,
                enabled,
                failed,
                running
        );
    }
}
