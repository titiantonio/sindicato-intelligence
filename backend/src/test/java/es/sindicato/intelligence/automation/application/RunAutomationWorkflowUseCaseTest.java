package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
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
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 1);

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);
        when(classifications.execute(1)).thenReturn(new AutomationRunResult(1, 1, 0, 0, List.of()));

        AutomationRunResult result = new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertEquals(1, result.processedCount());
        assertFalse(setting.isRunning());
        assertEquals(1, setting.getLastProcessedCount());
        assertEquals(1, setting.getLastSuccessCount());
        assertTrue(setting.getNextRunAt().isAfter(setting.getLastRunAt()));
        verify(classifications).execute(1);
    }

    @Test
    void skipsWorkflowAlreadyRunning() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        ProcessPendingClassificationsUseCase classifications = mock(ProcessPendingClassificationsUseCase.class);
        ProcessPendingEventDetectionUseCase eventDetection = mock(ProcessPendingEventDetectionUseCase.class);
        ProcessPendingEventAnalysisUseCase analysis = mock(ProcessPendingEventAnalysisUseCase.class);
        AutomationWorkflowSetting setting = setting(AutomationWorkflowCode.WF02_CLASSIFICATION, 1);
        setting.markRunning(OffsetDateTime.parse("2026-06-16T10:00:00Z"));

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));

        AutomationRunResult result = new RunAutomationWorkflowUseCase(repository, classifications, eventDetection, analysis)
                .execute(AutomationWorkflowCode.WF02_CLASSIFICATION);

        assertEquals(1, result.skippedCount());
        verify(classifications, never()).execute(1);
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
}
