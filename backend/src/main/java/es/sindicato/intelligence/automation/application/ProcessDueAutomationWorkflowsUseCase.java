package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class ProcessDueAutomationWorkflowsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessDueAutomationWorkflowsUseCase.class);

    private final AutomationWorkflowSettingRepository repository;
    private final RunAutomationWorkflowUseCase runAutomationWorkflowUseCase;

    public ProcessDueAutomationWorkflowsUseCase(
            AutomationWorkflowSettingRepository repository,
            RunAutomationWorkflowUseCase runAutomationWorkflowUseCase
    ) {
        this.repository = repository;
        this.runAutomationWorkflowUseCase = runAutomationWorkflowUseCase;
    }

    public int execute() {
        List<AutomationWorkflowSetting> dueWorkflows = repository.findDue(OffsetDateTime.now());
        for (AutomationWorkflowSetting setting : dueWorkflows) {
            try {
                runAutomationWorkflowUseCase.execute(setting.getWorkflowCode());
            } catch (RuntimeException exception) {
                log.error("due automation workflow failed without blocking next workflows: workflowCode={}, reason={}",
                        setting.getWorkflowCode(), exception.getMessage(), exception);
            }
        }
        return dueWorkflows.size();
    }
}
