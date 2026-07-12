package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RecoverStaleAutomationWorkflowsUseCase {

    private static final Logger log = LoggerFactory.getLogger(RecoverStaleAutomationWorkflowsUseCase.class);
    private static final String STALE_RUNNING_ERROR = "Workflow recuperado tras quedar bloqueado en running=true";

    private final AutomationWorkflowSettingRepository repository;
    private final int staleRunningTimeoutMinutes;

    public RecoverStaleAutomationWorkflowsUseCase(
            AutomationWorkflowSettingRepository repository,
            @Value("${app.automation.stale-running-timeout-minutes:30}") int staleRunningTimeoutMinutes
    ) {
        this.repository = repository;
        this.staleRunningTimeoutMinutes = Math.max(1, staleRunningTimeoutMinutes);
    }

    @Transactional
    public int execute() {
        OffsetDateTime now = OffsetDateTime.now();
        OffsetDateTime threshold = now.minusMinutes(staleRunningTimeoutMinutes);
        List<AutomationWorkflowSetting> staleWorkflows = repository.findAll().stream()
                .filter(setting -> isStaleRunning(setting, threshold))
                .toList();

        for (AutomationWorkflowSetting setting : staleWorkflows) {
            setting.recoverStaleRunning(STALE_RUNNING_ERROR, now);
            repository.save(setting);
            log.warn("automation workflow stale running state recovered: workflowCode={}, lastRunAt={}, timeoutMinutes={}",
                    setting.getWorkflowCode(), setting.getLastRunAt(), staleRunningTimeoutMinutes);
        }

        return staleWorkflows.size();
    }

    private boolean isStaleRunning(AutomationWorkflowSetting setting, OffsetDateTime threshold) {
        return setting.isEnabled()
                && setting.isRunning()
                && (setting.getLastRunAt() == null || !setting.getLastRunAt().isAfter(threshold));
    }
}
