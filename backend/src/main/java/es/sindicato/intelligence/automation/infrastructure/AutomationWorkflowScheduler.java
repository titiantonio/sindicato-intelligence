package es.sindicato.intelligence.automation.infrastructure;

import es.sindicato.intelligence.automation.application.ProcessDueAutomationWorkflowsUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.automation.scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class AutomationWorkflowScheduler {

    private static final Logger log = LoggerFactory.getLogger(AutomationWorkflowScheduler.class);

    private final ProcessDueAutomationWorkflowsUseCase processDueAutomationWorkflowsUseCase;

    public AutomationWorkflowScheduler(ProcessDueAutomationWorkflowsUseCase processDueAutomationWorkflowsUseCase) {
        this.processDueAutomationWorkflowsUseCase = processDueAutomationWorkflowsUseCase;
    }

    @Scheduled(
            fixedDelayString = "${app.automation.scheduler.fixed-delay-ms:5000}",
            initialDelayString = "${app.automation.scheduler.initial-delay-ms:60000}"
    )
    public void processDueWorkflows() {
        int processedWorkflows = processDueAutomationWorkflowsUseCase.execute();
        if (processedWorkflows > 0) {
            log.info("automation workflow scheduler completed: processedWorkflows={}", processedWorkflows);
        }
    }
}
