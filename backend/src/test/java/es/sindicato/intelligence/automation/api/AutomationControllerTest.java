package es.sindicato.intelligence.automation.api;

import es.sindicato.intelligence.automation.application.AutomationRunError;
import es.sindicato.intelligence.automation.application.AutomationRunResult;
import es.sindicato.intelligence.automation.application.AutomationOverview;
import es.sindicato.intelligence.automation.application.GetAutomationOverviewUseCase;
import es.sindicato.intelligence.automation.application.GetAutomationSettingUseCase;
import es.sindicato.intelligence.automation.application.ListAutomationSettingsUseCase;
import es.sindicato.intelligence.automation.application.ListWorkflowOperationsUseCase;
import es.sindicato.intelligence.automation.application.ProcessPendingEventAnalysisUseCase;
import es.sindicato.intelligence.automation.application.RunPendingAnalysisCommand;
import es.sindicato.intelligence.automation.application.RunAutomationWorkflowUseCase;
import es.sindicato.intelligence.automation.application.UpdateAutomationSettingUseCase;
import es.sindicato.intelligence.automation.application.UpdateAutomationWorkflowSettingCommand;
import es.sindicato.intelligence.automation.application.WorkflowOperationView;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowCode;
import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;
import es.sindicato.intelligence.core.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.time.OffsetDateTime;
import java.util.Map;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AutomationController.class)
@Import(SecurityConfig.class)
class AutomationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private ProcessPendingEventAnalysisUseCase processPendingEventAnalysisUseCase;

    @MockBean
    private ListAutomationSettingsUseCase listAutomationSettingsUseCase;

    @MockBean
    private GetAutomationSettingUseCase getAutomationSettingUseCase;

    @MockBean
    private UpdateAutomationSettingUseCase updateAutomationSettingUseCase;

    @MockBean
    private RunAutomationWorkflowUseCase runAutomationWorkflowUseCase;

    @MockBean
    private GetAutomationOverviewUseCase getAutomationOverviewUseCase;

    @MockBean
    private ListWorkflowOperationsUseCase listWorkflowOperationsUseCase;

    @Test
    void allowsEditorToRunPendingClassifications() throws Exception {
        when(runAutomationWorkflowUseCase.execute(AutomationWorkflowCode.WF02_CLASSIFICATION)).thenReturn(new AutomationRunResult(
                2,
                1,
                1,
                0,
                List.of(new AutomationRunError(7L, "classification failed"))
        ));

        mockMvc.perform(post("/api/v1/automation/classifications/run")
                        .with(jwt().authorities(() -> "ROLE_EDITOR")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processedCount").value(2))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.failedCount").value(1))
                .andExpect(jsonPath("$.skippedCount").value(0))
                .andExpect(jsonPath("$.errors[0].entityId").value(7))
                .andExpect(jsonPath("$.errors[0].message").value("classification failed"));
    }

    @Test
    void rejectsAnonymousAutomationRun() throws Exception {
        mockMvc.perform(post("/api/v1/automation/events/run"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void forwardsOptionalEventIdForAnalysisRun() throws Exception {
        when(processPendingEventAnalysisUseCase.execute(new RunPendingAnalysisCommand(12L))).thenReturn(new AutomationRunResult(1, 1, 0, 0, List.of()));

        mockMvc.perform(post("/api/v1/automation/analysis/run")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "eventId": 12
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.processedCount").value(1))
                .andExpect(jsonPath("$.successCount").value(1));

        verify(processPendingEventAnalysisUseCase).execute(new RunPendingAnalysisCommand(12L));
    }

    @Test
    void allowsAdminToListSettings() throws Exception {
        when(listAutomationSettingsUseCase.execute()).thenReturn(List.of(setting()));

        mockMvc.perform(get("/api/v1/automation/settings")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workflowCode").value("WF02_CLASSIFICATION"))
                .andExpect(jsonPath("$[0].enabled").value(true))
                .andExpect(jsonPath("$[0].batchSize").value(1));
    }

    @Test
    void allowsAdminToReadAutomationOverview() throws Exception {
        when(getAutomationOverviewUseCase.execute()).thenReturn(new AutomationOverview(
                "WF01_CAPTURE_NEWS",
                "WF-01-Capture-News",
                "EXTERNAL_N8N",
                List.of(setting()),
                1,
                0,
                0
        ));

        mockMvc.perform(get("/api/v1/automation/overview")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.n8nWorkflowName").value("WF-01-Capture-News"))
                .andExpect(jsonPath("$.n8nStatus").value("EXTERNAL_N8N"))
                .andExpect(jsonPath("$.backendEnabledCount").value(1))
                .andExpect(jsonPath("$.backendWorkflows[0].workflowCode").value("WF02_CLASSIFICATION"));
    }

    @Test
    void allowsAdminToListWorkflowOperations() throws Exception {
        when(listWorkflowOperationsUseCase.execute(java.time.LocalDate.parse("2026-06-18"))).thenReturn(List.of(new WorkflowOperationView(
                "AI-1",
                "WF02_CLASSIFICATION",
                "CLASSIFICATION",
                "SUCCESS",
                "NEWS",
                7L,
                OffsetDateTime.parse("2026-06-18T10:00:00Z"),
                120L,
                "WF02_CLASSIFICATION",
                "GeminiAIProvider",
                "gemini-1.5-flash",
                null,
                Map.of("category", "OTROS", "finalNewsStatus", "DISCARDED")
        )));

        mockMvc.perform(get("/api/v1/automation/operations?date=2026-06-18")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].workflowCode").value("WF02_CLASSIFICATION"))
                .andExpect(jsonPath("$[0].details.category").value("OTROS"))
                .andExpect(jsonPath("$[0].details.finalNewsStatus").value("DISCARDED"));
    }

    @Test
    void rejectsEditorWhenUpdatingSettings() throws Exception {
        mockMvc.perform(put("/api/v1/automation/settings/WF02_CLASSIFICATION")
                        .with(jwt().authorities(() -> "ROLE_EDITOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "enabled": true,
                                  "intervalSeconds": 600,
                                  "batchSize": 1
                                }
                                """))
                .andExpect(status().isForbidden());
    }

    @Test
    void allowsAdminToUpdateSettings() throws Exception {
        when(updateAutomationSettingUseCase.execute(
                AutomationWorkflowCode.WF02_CLASSIFICATION,
                new UpdateAutomationWorkflowSettingCommand(true, 900, 2)
        )).thenReturn(setting(true, 900, 2));

        mockMvc.perform(put("/api/v1/automation/settings/WF02_CLASSIFICATION")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "enabled": true,
                                  "intervalSeconds": 900,
                                  "batchSize": 2
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.intervalSeconds").value(900))
                .andExpect(jsonPath("$.batchSize").value(2));
    }

    private AutomationWorkflowSetting setting() {
        return setting(true, 600, 1);
    }

    private AutomationWorkflowSetting setting(boolean enabled, int intervalSeconds, int batchSize) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-16T10:00:00Z");
        return new AutomationWorkflowSetting(
                AutomationWorkflowCode.WF02_CLASSIFICATION,
                enabled,
                intervalSeconds,
                batchSize,
                false,
                null,
                null,
                null,
                now.plusMinutes(10),
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
