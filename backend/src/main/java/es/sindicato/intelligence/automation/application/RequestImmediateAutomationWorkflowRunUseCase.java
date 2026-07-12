package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class RequestImmediateAutomationWorkflowRunUseCase {

    private static final Logger log = LoggerFactory.getLogger(RequestImmediateAutomationWorkflowRunUseCase.class);

    private final AutomationWorkflowSettingRepository repository;

    public RequestImmediateAutomationWorkflowRunUseCase(AutomationWorkflowSettingRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public boolean execute(AutomationWorkflowCode workflowCode) {
        Objects.requireNonNull(workflowCode, "workflowCode is required");
        OffsetDateTime now = OffsetDateTime.now();
        AutomationWorkflowSetting setting = repository.findByCode(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("automation workflow setting not found: " + workflowCode));

        boolean requested = setting.requestImmediateRun(now);
        if (requested) {
            repository.save(setting);
            log.info("automation workflow immediate run requested: workflowCode={}", workflowCode);
        } else {
            log.info("automation workflow immediate run skipped: workflowCode={}, enabled={}, running={}, nextRunAt={}",
                    workflowCode, setting.isEnabled(), setting.isRunning(), setting.getNextRunAt());
        }
        return requested;
    }
}
