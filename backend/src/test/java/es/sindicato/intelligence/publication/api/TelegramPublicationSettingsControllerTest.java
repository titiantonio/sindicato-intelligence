package es.sindicato.intelligence.publication.api;

import es.sindicato.intelligence.core.config.SecurityConfig;
import es.sindicato.intelligence.publication.application.GetTelegramPublicationSettingsUseCase;
import es.sindicato.intelligence.publication.application.UpdateTelegramPublicationSettingsCommand;
import es.sindicato.intelligence.publication.application.UpdateTelegramPublicationSettingsUseCase;
import es.sindicato.intelligence.publication.domain.TelegramPublicationSettings;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.OffsetDateTime;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TelegramPublicationSettingsController.class)
@Import(SecurityConfig.class)
class TelegramPublicationSettingsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private GetTelegramPublicationSettingsUseCase getSettingsUseCase;

    @MockBean
    private UpdateTelegramPublicationSettingsUseCase updateSettingsUseCase;

    @Test
    void allowsAdminToReadTelegramSettingsWithoutExposingToken() throws Exception {
        when(getSettingsUseCase.execute()).thenReturn(settings(true, "123456:secret-token", "chat-id"));

        mockMvc.perform(get("/api/v1/settings/telegram")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.botTokenConfigured").value(true))
                .andExpect(jsonPath("$.botTokenPreview").value("1234...oken"))
                .andExpect(jsonPath("$.readyToPublish").value(true));
    }

    @Test
    void rejectsEditorWhenUpdatingTelegramSettings() throws Exception {
        mockMvc.perform(put("/api/v1/settings/telegram")
                        .with(jwt().authorities(() -> "ROLE_EDITOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isForbidden());
    }

    @Test
    void allowsAdminToUpdateTelegramSettings() throws Exception {
        when(updateSettingsUseCase.execute(new UpdateTelegramPublicationSettingsCommand(
                true,
                "https://api.telegram.org",
                "token",
                "chat-id",
                true
        ))).thenReturn(settings(true, "token", "chat-id"));

        mockMvc.perform(put("/api/v1/settings/telegram")
                        .with(jwt().authorities(() -> "ROLE_ADMIN"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validPayload()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.readyToPublish").value(true));
    }

    private String validPayload() {
        return """
                {
                  "enabled": true,
                  "baseUrl": "https://api.telegram.org",
                  "botToken": "token",
                  "chatId": "chat-id",
                  "disableWebPagePreview": true
                }
                """;
    }

    private TelegramPublicationSettings settings(boolean enabled, String botToken, String chatId) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-16T10:00:00Z");
        return new TelegramPublicationSettings(
                (short) 1,
                enabled,
                "https://api.telegram.org",
                botToken,
                chatId,
                true,
                now,
                now
        );
    }
}
