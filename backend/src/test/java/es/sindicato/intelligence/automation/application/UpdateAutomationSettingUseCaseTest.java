package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSettingRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UpdateAutomationSettingUseCaseTest {

    @Test
    void recordsAuditWhenAutomationSettingIsUpdated() {
        AutomationWorkflowSettingRepository repository = mock(AutomationWorkflowSettingRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        UpdateAutomationSettingUseCase useCase = new UpdateAutomationSettingUseCase(repository, audit);
        AutomationWorkflowSetting setting = setting();

        when(repository.findByCode(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(Optional.of(setting));
        when(repository.save(setting)).thenReturn(setting);

        AutomationWorkflowSetting result = useCase.execute(
                AutomationWorkflowCode.WF02_CLASSIFICATION,
                new UpdateAutomationWorkflowSettingCommand(false, 900, 3)
        );

        assertEquals(900, result.getIntervalSeconds());
        assertEquals(3, result.getBatchSize());
        verify(audit).record(eq("AUTOMATION_SETTING_UPDATED"), eq("AUTOMATION"), eq(null), any(), any());
    }

    private AutomationWorkflowSetting setting() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-16T10:00:00Z");
        return new AutomationWorkflowSetting(
                AutomationWorkflowCode.WF02_CLASSIFICATION,
                true,
                600,
                1,
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
