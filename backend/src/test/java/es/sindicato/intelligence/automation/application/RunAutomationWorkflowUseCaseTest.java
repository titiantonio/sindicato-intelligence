package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionOperations;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RunAutomationWorkflowUseCaseTest {

    @Test
    void runsClassificationWithConfiguredBatchSizeAndStoresResult() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        AiModelExecutionCoordinator coordinator = coordinator();
        TransactionOperations transactionOperations = transactionOperations();
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 1);

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);
        when(classifications.execute(1)).thenReturn(new AutomationRunResult(1, 1, 0, 0, List.of()));

        AutomationRunResult result = new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis, audit, transactionOperations, coordinator)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertEquals(1, result.processedCount());
        assertFalse(setting.isRunning());
        assertEquals(1, setting.getLastProcessedCount());
        assertEquals(1, setting.getLastSuccessCount());
        assertEquals(setting.getUpdatedAt(), setting.getNextRunAt());
        verify(classifications).execute(1);
        verify(audit).record(org.mockito.ArgumentMatchers.eq("AUTOMATION_RUN_COMPLETED"), org.mockito.ArgumentMatchers.eq("AUTOMATION"), org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void storesPartialClassificationFailuresAndSchedulesNextRun() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        AiModelExecutionCoordinator coordinator = coordinator();
        TransactionOperations transactionOperations = transactionOperations();
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 10);

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);
        when(classifications.execute(10)).thenReturn(new AutomationRunResult(
                10,
                8,
                2,
                0,
                List.of(
                        new AutomationRunError(100L, "Gemini classification request failed with HTTP 500"),
                        new AutomationRunError(101L, "Gemini classification request failed with HTTP 503")
                )
        ));

        AutomationRunResult result = new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis, audit, transactionOperations, coordinator)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertEquals(10, result.processedCount());
        assertEquals(8, result.successCount());
        assertEquals(2, result.failedCount());
        assertFalse(setting.isRunning());
        assertEquals(10, setting.getLastProcessedCount());
        assertEquals(8, setting.getLastSuccessCount());
        assertEquals(2, setting.getLastFailedCount());
        assertEquals("Gemini classification request failed with HTTP 500", setting.getLastError());
        assertFalse(setting.getNextRunAt().isBefore(setting.getLastRunAt()));
        verify(classifications).execute(10);
        verify(audit).record(org.mockito.ArgumentMatchers.eq("AUTOMATION_RUN_COMPLETED"), org.mockito.ArgumentMatchers.eq("AUTOMATION"), org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.any());
    }

    @Test
    void skipsWorkflowAlreadyRunning() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        AiModelExecutionCoordinator coordinator = coordinator();
        TransactionOperations transactionOperations = transactionOperations();
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 1);
        setting.markRunning(OffsetDateTime.parse("2026-06-16T10:00:00Z"));

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));

        AutomationRunResult result = new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis, audit, transactionOperations, coordinator)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertEquals(1, result.skippedCount());
        verify(classifications, never()).execute(1);
    }

    @Test
    void reschedulesEventDetectionImmediatelyWhenFullBatchIsProcessed() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        AiModelExecutionCoordinator coordinator = coordinator();
        TransactionOperations transactionOperations = transactionOperations();
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF03_EVENT_DETECTION, 3);

        when(repository.findByCode(AutomationWorkflowCode.WF03_EVENT_DETECTION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);
        when(eventDetection.execute(3)).thenReturn(new AutomationRunResult(3, 3, 0, 0, List.of()));

        new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis, audit, transactionOperations, coordinator)
                .execute(AutomationWorkflowCode.WF03_EVENT_DETECTION);

        assertEquals(setting.getUpdatedAt(), setting.getNextRunAt());
        verify(eventDetection).execute(3);
    }

    @Test
    void doesNotRescheduleClassificationImmediatelyWhenFullBatchHasFailures() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        AiModelExecutionCoordinator coordinator = coordinator();
        TransactionOperations transactionOperations = transactionOperations();
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 3);

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);
        when(classifications.execute(3)).thenReturn(new AutomationRunResult(3, 2, 1, 0, List.of(new AutomationRunError(10L, "temporary failure"))));

        new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis, audit, transactionOperations, coordinator)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertTrue(setting.getNextRunAt().isAfter(setting.getUpdatedAt()));
        verify(classifications).execute(3);
    }

    @Test
    void reschedulesClassificationImmediatelyWhenFullBatchHasOnlySuccessfulWork() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        AiModelExecutionCoordinator coordinator = coordinator();
        TransactionOperations transactionOperations = transactionOperations();
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 3);

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);
        when(classifications.execute(3)).thenReturn(new AutomationRunResult(3, 3, 0, 0, List.of()));

        new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis, audit, transactionOperations, coordinator)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertEquals(setting.getUpdatedAt(), setting.getNextRunAt());
        verify(classifications).execute(3);
    }

    @Test
    void doesNotRescheduleClassificationImmediatelyWhenAnotherWorkflowIsDue() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        AiModelExecutionCoordinator coordinator = coordinator();
        TransactionOperations transactionOperations = transactionOperations();
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 3);
        AutomationWorkflowSetting dueAnalysis = setting(AutomationWorkflowCode.WF04_ANALYSIS, 1);

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);
        when(repository.findDue(any())).thenReturn(List.of(dueAnalysis));
        when(classifications.execute(3)).thenReturn(new AutomationRunResult(3, 3, 0, 0, List.of()));

        new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis, audit, transactionOperations, coordinator)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertTrue(setting.getNextRunAt().isAfter(setting.getUpdatedAt()));
        verify(classifications).execute(3);
    }

    @Test
    void doesNotRescheduleClassificationImmediatelyWhenFullBatchIsOnlySkipped() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        AiModelExecutionCoordinator coordinator = coordinator();
        TransactionOperations transactionOperations = transactionOperations();
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 3);

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);
        when(classifications.execute(3)).thenReturn(new AutomationRunResult(3, 0, 0, 3, List.of()));

        new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis, audit, transactionOperations, coordinator)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertTrue(setting.getNextRunAt().isAfter(setting.getUpdatedAt()));
        verify(classifications).execute(3);
    }

    private AutomationWorkflowSetting setting(AutomationWorkflowCode code, int batchSize) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-16T10:00:00Z");
        return new AutomationWorkflowSetting(
                code,
                true,
                600,
                batchSize,
                false,
                null,
                null,
                null,
                now,
                0,
                0,
                0,
                0,
                null,
                now,
                now
        );
    }

    private TransactionOperations transactionOperations() {
        TransactionOperations transactionOperations = mock(TransactionOperations.class);
        when(transactionOperations.execute(any())).thenAnswer(invocation -> {
            TransactionCallback<?> callback = invocation.getArgument(0);
            return callback.doInTransaction(null);
        });
        return transactionOperations;
    }

    private AiModelExecutionCoordinator coordinator() {
        AiModelExecutionCoordinator coordinator = mock(AiModelExecutionCoordinator.class);
        when(coordinator.execute(any(), any())).thenAnswer(invocation -> invocation.<java.util.function.Supplier<?>>getArgument(1).get());
        return coordinator;
    }
}
