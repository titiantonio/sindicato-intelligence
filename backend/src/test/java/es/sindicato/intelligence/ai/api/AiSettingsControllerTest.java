package es.sindicato.intelligence.ai.api;

import es.sindicato.intelligence.ai.application.AiModelOption;
import es.sindicato.intelligence.ai.application.AiProviderSettingView;
import es.sindicato.intelligence.ai.application.AiWorkflowSettingView;
import es.sindicato.intelligence.ai.application.ListAiProviderModelsUseCase;
import es.sindicato.intelligence.ai.application.ListAiProviderSettingsUseCase;
import es.sindicato.intelligence.ai.application.ListAiWorkflowSettingsUseCase;
import es.sindicato.intelligence.ai.application.UpdateAiProviderSettingCommand;
import es.sindicato.intelligence.ai.application.UpdateAiProviderSettingUseCase;
import es.sindicato.intelligence.ai.application.UpdateAiWorkflowSettingCommand;
import es.sindicato.intelligence.ai.application.UpdateAiWorkflowSettingUseCase;
import es.sindicato.intelligence.core.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiSettingsController.class)
@Import(SecurityConfig.class)
class AiSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private ListAiProviderSettingsUseCase listAiProviderSettingsUseCase;

    @MockBean
    private UpdateAiProviderSettingUseCase updateAiProviderSettingUseCase;

    @MockBean
    private ListAiProviderModelsUseCase listAiProviderModelsUseCase;

    @MockBean
    private ListAiWorkflowSettingsUseCase listAiWorkflowSettingsUseCase;

    @MockBean
    private UpdateAiWorkflowSettingUseCase updateAiWorkflowSettingUseCase;

    @Test
    void allowsAdminToListProvidersWithoutExposingApiKey() throws Exception {
        when(listAiProviderSettingsUseCase.execute()).thenReturn(List.of(provider()));

        mockMvc.perform(get("/api/v1/ai/providers")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].providerCode").value("gemini"))
                .andExpect(jsonPath("$[0].apiKeyConfigured").value(true))
                .andExpect(jsonPath("$[0].apiKeyPreview").value("abcd...wxyz"))
                .andExpect(jsonPath("$[0].apiKey").doesNotExist());
    }

    @Test
    void rejectsEditorFromProviderSettings() throws Exception {
        mockMvc.perform(get("/api/v1/ai/providers")
                        .with(jwt().authorities(() -> "ROLE_EDITOR")))
                .andExpect(status().isForbidden());
    }

    @Test
    void allowsAdminToUpdateProvider() throws Exception {
        when(updateAiProviderSettingUseCase.execute(
                "gemini",
                new UpdateAiProviderSettingCommand(true, "new-key", false)
        )).thenReturn(provider());

        mockMvc.perform(put("/api/v1/ai/providers/gemini")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "enabled": true,
                                  "apiKey": "new-key"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.providerCode").value("gemini"));
    }

    @Test
    void allowsAdminToClearProviderApiKey() throws Exception {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-24T10:00:00Z");
        when(updateAiProviderSettingUseCase.execute(
                "gemini",
                new UpdateAiProviderSettingCommand(false, null, true)
        )).thenReturn(new AiProviderSettingView("gemini", "Google Gemini", false, false, null, now, now));

        mockMvc.perform(put("/api/v1/ai/providers/gemini")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "enabled": false,
                                  "clearApiKey": true
                                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.apiKeyConfigured").value(false))
                .andExpect(jsonPath("$.apiKey").doesNotExist());
    }

    @Test
    void allowsAdminToListProviderModels() throws Exception {
        when(listAiProviderModelsUseCase.execute("gemini", "test-key"))
                .thenReturn(List.of(new AiModelOption("models/gemini-2.5-flash", "Gemini 2.5 Flash")));

        mockMvc.perform(post("/api/v1/ai/providers/gemini/models")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "apiKey": "test-key"
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("models/gemini-2.5-flash"));
    }

    @Test
    void allowsAdminToUpdateWorkflowSettings() throws Exception {
        when(updateAiWorkflowSettingUseCase.execute(
                "WF04_ANALYSIS",
                new UpdateAiWorkflowSettingCommand("gemini", "models/gemini-2.5-flash", BigDecimal.valueOf(0.3), 2048, 60)
        )).thenReturn(workflow());

        mockMvc.perform(put("/api/v1/ai/workflow-settings/WF04_ANALYSIS")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "providerCode": "gemini",
                                  "modelName": "models/gemini-2.5-flash",
                                  "temperature": 0.3,
                                  "maxOutputTokens": 2048,
                                  "cooldownSeconds": 60
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.workflowCode").value("WF04_ANALYSIS"))
                .andExpect(jsonPath("$.providerCode").value("gemini"));
    }

    private AiProviderSettingView provider() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-24T10:00:00Z");
        return new AiProviderSettingView("gemini", "Google Gemini", true, true, "abcd...wxyz", now, now);
    }

    private AiWorkflowSettingView workflow() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-24T10:00:00Z");
        return new AiWorkflowSettingView("WF04_ANALYSIS", "gemini", "Google Gemini", "models/gemini-2.5-flash", BigDecimal.valueOf(0.3), 2048, 60, now, now);
    }
}
