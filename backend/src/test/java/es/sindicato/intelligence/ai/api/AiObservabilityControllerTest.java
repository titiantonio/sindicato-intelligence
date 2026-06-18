package es.sindicato.intelligence.ai.api;

import es.sindicato.intelligence.ai.application.AiMetricSummary;
import es.sindicato.intelligence.ai.application.AiMetricsSnapshot;
import es.sindicato.intelligence.ai.application.AiOperationMetricView;
import es.sindicato.intelligence.ai.application.ListAiMetricsUseCase;
import es.sindicato.intelligence.ai.application.ListAiPromptVersionsUseCase;
import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiPromptVersion;
import es.sindicato.intelligence.core.config.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AiObservabilityController.class)
@Import(SecurityConfig.class)
class AiObservabilityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JwtDecoder jwtDecoder;

    @MockBean
    private ListAiPromptVersionsUseCase listAiPromptVersionsUseCase;

    @MockBean
    private ListAiMetricsUseCase listAiMetricsUseCase;

    @Test
    void allowsAdminToListPromptVersions() throws Exception {
        when(listAiPromptVersionsUseCase.execute()).thenReturn(List.of(new AiPromptVersion(
                1L,
                "WF02_CLASSIFICATION",
                "Clasificacion de noticias",
                "classification",
                "1.0.0",
                "checksum",
                true,
                OffsetDateTime.parse("2026-06-18T10:00:00Z")
        )));

        mockMvc.perform(get("/api/v1/ai/prompts")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].promptKey").value("WF02_CLASSIFICATION"))
                .andExpect(jsonPath("$[0].version").value("1.0.0"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void allowsAdminToListAiMetrics() throws Exception {
        when(listAiMetricsUseCase.execute(10)).thenReturn(new AiMetricsSnapshot(
                new AiMetricSummary(1, 1, 0, 120),
                List.of(new AiOperationMetricView(
                        5L,
                        "CLASSIFICATION",
                        "WF02_CLASSIFICATION",
                        "GeminiAIProvider",
                        "gemini-1.5-flash",
                        AiMetricStatus.SUCCESS,
                        "NEWS",
                        11L,
                        120,
                        null,
                        OffsetDateTime.parse("2026-06-18T10:00:00Z")
                ))
        ));

        mockMvc.perform(get("/api/v1/ai/metrics?limit=10")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOperations").value(1))
                .andExpect(jsonPath("$.successCount").value(1))
                .andExpect(jsonPath("$.recentMetrics[0].operationType").value("CLASSIFICATION"))
                .andExpect(jsonPath("$.recentMetrics[0].status").value("SUCCESS"));
    }

    @Test
    void allowsAdminToListDailyAiMetrics() throws Exception {
        when(listAiMetricsUseCase.execute(LocalDate.parse("2026-06-18"))).thenReturn(new AiMetricsSnapshot(
                new AiMetricSummary(
                        3,
                        2,
                        1,
                        250,
                        450,
                        67,
                        33,
                        2,
                        1,
                        1,
                        200,
                        1,
                        17,
                        -17,
                        50
                ),
                List.of(new AiOperationMetricView(
                        6L,
                        "ANALYSIS",
                        "WF04_ANALYSIS",
                        "GeminiAnalysisAIProvider",
                        "models/gemma-4-31b-it",
                        AiMetricStatus.FAILED,
                        "EVENT",
                        15L,
                        450,
                        "Respuesta IA invalida",
                        OffsetDateTime.parse("2026-06-18T11:00:00Z")
                ))
        ));

        mockMvc.perform(get("/api/v1/ai/metrics?date=2026-06-18")
                        .with(jwt().authorities(() -> "ROLE_ADMIN")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalOperations").value(3))
                .andExpect(jsonPath("$.p95LatencyMs").value(450))
                .andExpect(jsonPath("$.successRate").value(67))
                .andExpect(jsonPath("$.previousTotalOperations").value(2))
                .andExpect(jsonPath("$.totalDifference").value(1))
                .andExpect(jsonPath("$.recentMetrics[0].model").value("models/gemma-4-31b-it"));
    }

    @Test
    void rejectsEditorFromAiObservability() throws Exception {
        mockMvc.perform(get("/api/v1/ai/prompts")
                        .with(jwt().authorities(() -> "ROLE_EDITOR")))
                .andExpect(status().isForbidden());
    }
}
