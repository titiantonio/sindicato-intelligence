package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionOperations;

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
    private final TransactionOperations transactionOperations;
    private final AiModelExecutionCoordinator aiModelExecutionCoordinator;

    public RunAutomationWorkflowUseCase(
            AutomationWorkflowSettingRepository repository,
            ProcessPendingClassificationsUseCase processPendingClassificationsUseCase,
            ProcessPendingEventDetectionUseCase processPendingEventDetectionUseCase,
            ProcessPendingEventAnalysisUseCase processPendingEventAnalysisUseCase,
            RecordAuditLogUseCase recordAuditLogUseCase,
            TransactionOperations transactionOperations,
            AiModelExecutionCoordinator aiModelExecutionCoordinator
    ) {
        this.repository = repository;
        this.processPendingClassificationsUseCase = processPendingClassificationsUseCase;
        this.processPendingEventDetectionUseCase = processPendingEventDetectionUseCase;
        this.processPendingEventAnalysisUseCase = processPendingEventAnalysisUseCase;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
        this.transactionOperations = transactionOperations;
        this.aiModelExecutionCoordinator = aiModelExecutionCoordinator;
    }

    public AutomationRunResult execute(AutomationWorkflowCode workflowCode) {
        AutomationWorkflowSetting setting = repository.findByCode(workflowCode)
                .orElseThrow(() -> new IllegalArgumentException("automation workflow setting not found: " + workflowCode));

        if (setting.isRunning()) {
            log.warn("automation workflow skipped because it is already running: workflowCode={}", workflowCode);
            return new AutomationRunResult(0, 0, 0, 1, List.of());
        }

        OffsetDateTime startedAt = OffsetDateTime.now();
        AutomationWorkflowSetting runningSetting = markRunning(setting, startedAt);

        try {
            AutomationRunResult result = aiModelExecutionCoordinator.execute(
                    aiWorkflowCode(workflowCode),
                    () -> executeBatch(workflowCode, runningSetting.getBatchSize())
            );
            markCompleted(runningSetting, workflowCode, result, OffsetDateTime.now());
            log.info("automation workflow completed: workflowCode={}, processed={}, success={}, failed={}, skipped={}",
                    workflowCode, result.processedCount(), result.successCount(), result.failedCount(), result.skippedCount());
            return result;
        } catch (RuntimeException exception) {
            markFailed(runningSetting, workflowCode, exception, OffsetDateTime.now());
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

    private String aiWorkflowCode(AutomationWorkflowCode workflowCode) {
        return switch (workflowCode) {
            case WF02_CLASSIFICATION -> "WF02_CLASSIFICATION";
            case WF03_EVENT_DETECTION -> "WF03_EVENT_MATCHING";
            case WF04_ANALYSIS -> "WF04_ANALYSIS";
        };
    }

    private AutomationWorkflowSetting markRunning(AutomationWorkflowSetting setting, OffsetDateTime startedAt) {
        return transactionOperations.execute(status -> {
            setting.markRunning(startedAt);
            return repository.save(setting);
        });
    }

    private void markCompleted(
            AutomationWorkflowSetting setting,
            AutomationWorkflowCode workflowCode,
            AutomationRunResult result,
            OffsetDateTime completedAt
    ) {
        transactionOperations.execute(status -> {
            setting.markCompleted(result, completedAt);
            if (shouldContinueImmediately(setting, workflowCode, result, completedAt)) {
                setting.requestImmediateRun(completedAt);
                log.info("automation workflow rescheduled immediately after full batch: workflowCode={}, batchSize={}",
                        workflowCode, setting.getBatchSize());
            }
            repository.save(setting);
            recordAuditLogUseCase.record(
                    "AUTOMATION_RUN_COMPLETED",
                    "AUTOMATION",
                    null,
                    null,
                    AuditDetailFormatter.automationRunCompleted(workflowCode.name(), result.processedCount(), result.successCount(), result.failedCount(), result.skippedCount())
            );
            return null;
        });
    }

    private boolean shouldContinueImmediately(
            AutomationWorkflowSetting setting,
            AutomationWorkflowCode workflowCode,
            AutomationRunResult result,
            OffsetDateTime completedAt
    ) {
        return (workflowCode == AutomationWorkflowCode.WF02_CLASSIFICATION || workflowCode == AutomationWorkflowCode.WF03_EVENT_DETECTION)
                && result.failedCount() == 0
                && result.processedCount() - result.skippedCount() >= setting.getBatchSize()
                && setting.getNextRunAt().isAfter(completedAt)
                && !hasOtherDueWorkflows(setting, completedAt);
    }

    private boolean hasOtherDueWorkflows(AutomationWorkflowSetting completedSetting, OffsetDateTime now) {
        return repository.findDue(now)
                .stream()
                .anyMatch(candidate -> candidate.getWorkflowCode() != completedSetting.getWorkflowCode());
    }

    private void markFailed(
            AutomationWorkflowSetting setting,
            AutomationWorkflowCode workflowCode,
            RuntimeException exception,
            OffsetDateTime failedAt
    ) {
        transactionOperations.execute(status -> {
            setting.markFailed(exception.getMessage(), failedAt);
            repository.save(setting);
            recordAuditLogUseCase.record(
                    "AUTOMATION_RUN_FAILED",
                    "AUTOMATION",
                    null,
                    null,
                    AuditDetailFormatter.automationRunFailed(workflowCode.name(), exception.getMessage())
            );
            return null;
        });
    }
}
