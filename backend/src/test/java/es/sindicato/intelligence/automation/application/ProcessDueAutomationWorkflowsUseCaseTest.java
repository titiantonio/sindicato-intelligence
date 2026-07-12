package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessDueAutomationWorkflowsUseCaseTest {

    @Test
    void runsOnlyDueWorkflowsReturnedByRepository() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        RunAutomationWorkflowUseCase runAutomationWorkflowUseCase = mock(RunAutomationWorkflowUseCase.class);
        RecoverStaleAutomationWorkflowsUseCase recoverStaleAutomationWorkflowsUseCase = mock(RecoverStaleAutomationWorkflowsUseCase.class);
        AutomationWorkflowSetting setting = setting();

        when(recoverStaleAutomationWorkflowsUseCase.execute()).thenReturn(0);
        when(repository.findDue(org.mockito.ArgumentMatchers.any())).thenReturn(List.of(setting));

        int processed = new ProcessDueAutomationWorkflowsUseCase(repository, runAutomationWorkflowUseCase, recoverStaleAutomationWorkflowsUseCase).execute();

        assertEquals(1, processed);
        verify(recoverStaleAutomationWorkflowsUseCase).execute();
        verify(runAutomationWorkflowUseCase).execute(AutomationWorkflowCode.WF03_EVENT_DETECTION);
    }

    private AutomationWorkflowSetting setting() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-16T10:00:00Z");
        return new AutomationWorkflowSetting(
                AutomationWorkflowCode.WF03_EVENT_DETECTION,
                true,
                600,
                3,
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
