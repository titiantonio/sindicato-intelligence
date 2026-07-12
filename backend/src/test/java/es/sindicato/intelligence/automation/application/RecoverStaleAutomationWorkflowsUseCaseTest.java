package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class RecoverStaleAutomationWorkflowsUseCaseTest {

    @Test
    void recoversEnabledWorkflowStuckInRunningState() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        AutomationWorkflowSetting setting = setting(true, true, OffsetDateTime.now().minusHours(2));

        when(repository.findAll()).thenReturn(List.of(setting));

        int recovered = new RecoverStaleAutomationWorkflowsUseCase(repository, 30).execute();

        assertEquals(1, recovered);
        assertFalse(setting.isRunning());
        assertTrue(!setting.getNextRunAt().isAfter(OffsetDateTime.now()));
        verify(repository).save(setting);
    }

    @Test
    void keepsRecentlyRunningWorkflowLocked() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        AutomationWorkflowSetting setting = setting(true, true, OffsetDateTime.now().minusMinutes(5));

        when(repository.findAll()).thenReturn(List.of(setting));

        int recovered = new RecoverStaleAutomationWorkflowsUseCase(repository, 30).execute();

        assertEquals(0, recovered);
        assertTrue(setting.isRunning());
        verify(repository, never()).save(setting);
    }

    private AutomationWorkflowSetting setting(boolean enabled, boolean running, OffsetDateTime lastRunAt) {
        OffsetDateTime now = OffsetDateTime.parse("2026-07-12T10:00:00Z");
        return new AutomationWorkflowSetting(
                AutomationWorkflowCode.WF02_CLASSIFICATION,
                enabled,
                120,
                10,
                running,
                lastRunAt,
                null,
                null,
                now.plusMinutes(10),
                10,
                4,
                6,
                0,
                null,
                now,
                now
        );
    }
}
