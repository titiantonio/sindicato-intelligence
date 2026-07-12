package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RequestImmediateAutomationWorkflowRunUseCaseTest {

    @Test
    void marksEnabledWorkflowAsDueImmediately() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        AutomationWorkflowSetting setting = setting(true, false, OffsetDateTime.now().plusMinutes(10));

        when(repository.findByCode(AutomationWorkflowCode.WF03_EVENT_DETECTION)).thenReturn(Optional.of(setting));

        boolean requested = new RequestImmediateAutomationWorkflowRunUseCase(repository)
                .execute(AutomationWorkflowCode.WF03_EVENT_DETECTION);

        assertTrue(requested);
        assertTrue(!setting.getNextRunAt().isAfter(OffsetDateTime.now()));
        verify(repository).save(setting);
    }

    @Test
    void skipsDisabledWorkflowWithoutSaving() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        AutomationWorkflowSetting setting = setting(false, false, OffsetDateTime.now().plusMinutes(10));

        when(repository.findByCode(AutomationWorkflowCode.WF03_EVENT_DETECTION)).thenReturn(Optional.of(setting));

        boolean requested = new RequestImmediateAutomationWorkflowRunUseCase(repository)
                .execute(AutomationWorkflowCode.WF03_EVENT_DETECTION);

        assertFalse(requested);
        verify(repository, never()).save(setting);
    }

    private AutomationWorkflowSetting setting(boolean enabled, boolean running, OffsetDateTime nextRunAt) {
        OffsetDateTime now = OffsetDateTime.parse("2026-07-11T10:00:00Z");
        return new AutomationWorkflowSetting(
                AutomationWorkflowCode.WF03_EVENT_DETECTION,
                enabled,
                600,
                3,
                running,
                null,
                null,
                null,
                nextRunAt,
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
