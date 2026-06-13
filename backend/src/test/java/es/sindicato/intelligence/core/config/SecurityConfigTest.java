package es.sindicato.intelligence.core.config;

import es.sindicato.intelligence.content.api.ContentController;
import es.sindicato.intelligence.content.application.ApproveContentUseCase;
import es.sindicato.intelligence.content.application.EditGeneratedContentUseCase;
import es.sindicato.intelligence.content.application.GenerateContentUseCase;
import es.sindicato.intelligence.content.application.GetGeneratedContentUseCase;
import es.sindicato.intelligence.content.application.ListGeneratedContentUseCase;
import es.sindicato.intelligence.content.application.RejectContentUseCase;
import es.sindicato.intelligence.health.HealthController;
import es.sindicato.intelligence.source.api.SourceController;
import es.sindicato.intelligence.source.application.CreateSourceUseCase;
import es.sindicato.intelligence.source.application.ListSourcesUseCase;
import es.sindicato.intelligence.source.application.UpdateSourceUseCase;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {HealthController.class, SourceController.class, ContentController.class})
@Import(SecurityConfig.class)
class SecurityConfigTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private CreateSourceUseCase createSourceUseCase;

    @MockBean
    private ListSourcesUseCase listSourcesUseCase;

    @MockBean
    private UpdateSourceUseCase updateSourceUseCase;

    @MockBean
    private GenerateContentUseCase generateContentUseCase;

    @MockBean
    private ApproveContentUseCase approveContentUseCase;

    @MockBean
    private RejectContentUseCase rejectContentUseCase;

    @MockBean
    private ListGeneratedContentUseCase listGeneratedContentUseCase;

    @MockBean
    private GetGeneratedContentUseCase getGeneratedContentUseCase;

    @MockBean
    private EditGeneratedContentUseCase editGeneratedContentUseCase;

    @Test
    void allowsHealthWithoutAuthentication() throws Exception {
        mockMvc.perform(get("/api/v1/health"))
                .andExpect(status().isOk());
    }

        @Test
        void allowsForgotPasswordWithoutAuthentication() throws Exception {
                mockMvc.perform(post("/api/v1/auth/forgot-password")
                                                .contentType(MediaType.APPLICATION_JSON)
                                                .content("""
                                                                {
                                                                    "email": "admin@sindicato.es"
                                                                }
                                                                """))
                                .andExpect(status().isNotFound());
        }

    @Test
    void rejectsProtectedEndpointWithoutAuthentication() throws Exception {
        mockMvc.perform(post("/api/v1/content/generate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void rejectsEditorOnAdminOnlyEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/sources")
                        .with(jwt().authorities(() -> "ROLE_EDITOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void allowsEditorOnContentEndpoint() throws Exception {
        mockMvc.perform(post("/api/v1/content/generate")
                        .with(jwt().authorities(() -> "ROLE_EDITOR"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
