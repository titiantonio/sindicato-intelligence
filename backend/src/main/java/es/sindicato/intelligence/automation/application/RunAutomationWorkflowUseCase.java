package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

@Service
public class RunAutomationWorkflowUseCase {

    private static final Logger log = LoggerFactory.getLogger(RunAutomationWorkflowUseCase.class);

    private final AutomationWorkflowSettingRepository repository;
    private final ProcessPendingClassificationsUseCase processPendingClassificationsUseCase;
    private final ProcessPendingEventDetectionUseCase processPendingEventDetectionUseCase;
    private final ProcessPendingEventAnalysisUseCase processPendingEventAnalysisUseCase;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public RunAutomationWorkflowUseCase(
            AutomationWorkflowSettingRepository repository,
            ProcessPendingClassificationsUseCase processPendingClassificationsUseCase,
            ProcessPendingEventDetectionUseCase processPendingEventDetectionUseCase,
            ProcessPendingEventAnalysisUseCase processPendingEventAnalysisUseCase,
            RecordAuditLogUseCase recordAuditLogUseCase
    ) {
        this.repository = repository;
        this.processPendingClassificationsUseCase = processPendingClassificationsUseCase;
        this.processPendingEventDetectionUseCase = processPendingEventDetectionUseCase;
        this.processPendingEventAnalysisUseCase = processPendingEventAnalysisUseCase;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public synchronized AutomationRunResult execute(AutomationWorkflowCode workflowCode) {
        AutomationWorkflowSetting setting = repository.findByCode(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("automation workflow setting not found: " + workflowCode));

        if (setting.isRunning()) {
            log.warn("automation workflow skipped because it is already running: workflowCode={}", workflowCode);
            return new AutomationRunResult(0, 0, 0, 1, List.of());
        }

        OffsetDateTime startedAt = OffsetDateTime.now();
        setting.markRunning(startedAt);
        repository.save(setting);

        try {
            AutomationRunResult result = executeBatch(workflowCode, setting.getBatchSize());
            setting.markCompleted(result, OffsetDateTime.now());
            repository.save(setting);
            recordAuditLogUseCase.record(
                    "AUTOMATION_RUN_COMPLETED",
                    "AUTOMATION",
                    null,
                    null,
                    AuditDetailFormatter.automationRunCompleted(workflowCode.name(), result.processedCount(), result.successCount(), result.failedCount(), result.skippedCount())
            );
            log.info("automation workflow completed: workflowCode={}, processed={}, success={}, failed={}, skipped={}",
                    workflowCode, result.processedCount(), result.successCount(), result.failedCount(), result.skippedCount());
            return result;
        } catch (RuntimeException exception) {
            setting.markFailed(exception.getMessage(), OffsetDateTime.now());
            repository.save(setting);
            recordAuditLogUseCase.record(
                    "AUTOMATION_RUN_FAILED",
                    "AUTOMATION",
                    null,
                    null,
                    AuditDetailFormatter.automationRunFailed(workflowCode.name(), exception.getMessage())
            );
            log.error("automation workflow failed: workflowCode={}, reason={}", workflowCode, exception.getMessage(), exception);
            throw exception;
        }
    }

    private AutomationRunResult executeBatch(AutomationWorkflowCode workflowCode, int batchSize) {
        return switch (workflowCode) {
            case WF02_CLASSIFICATION -> processPendingClassificationsUseCase.execute(batchSize);
            case WF03_EVENT_DETECTION -> processPendingEventDetectionUseCase.execute(batchSize);
            case WF04_ANALYSIS -> processPendingEventAnalysisUseCase.executePending(batchSize);
        };
    }
}
