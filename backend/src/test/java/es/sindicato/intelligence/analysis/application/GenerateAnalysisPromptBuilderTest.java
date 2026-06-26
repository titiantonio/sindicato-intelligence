package es.sindicato.intelligence.analysis.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

class GenerateAnalysisPromptBuilderTest {

    @Test
    void buildsOfficialWf04Prompt() {
        GenerateAnalysisPromptBuilder builder = new GenerateAnalysisPromptBuilder();

        GenerateAnalysisPrompt prompt = builder.build(new AnalysisAIRequest(
                10L,
                "Movilizacion sindical 0-3",
                "Evento sobre movilizacion sindical.",
                EventCategory.SINDICAL,
                Importance.MEDIUM,
                List.of(new AnalysisNewsItem(2L, "CCOO mantiene movilizaciones", "Resumen", "Contenido", OffsetDateTime.parse("2026-06-08T10:00:00Z"))),
                "",
                ""
        ));

        assertTrue(prompt.systemPrompt().contains("analista senior"));
        assertTrue(prompt.systemPrompt().contains("No inventes informacion"));
        assertTrue(prompt.userPrompt().contains("EVENTO"));
        assertTrue(prompt.userPrompt().contains("NOTICIAS"));
        assertTrue(prompt.userPrompt().contains("executiveSummary"));
        assertTrue(prompt.userPrompt().contains("recommendedMonitoring"));
        assertTrue(prompt.userPrompt().contains("CCOO mantiene movilizaciones"));
    }

    @Test
    void limitsNewsContextAndRequestsShortStableSpanishJson() {
        GenerateAnalysisPromptBuilder builder = new GenerateAnalysisPromptBuilder();
        String longContent = "contenido ".repeat(2_000);

        GenerateAnalysisPrompt prompt = builder.build(new AnalysisAIRequest(
                10L,
                "Resolucion definitiva de comisiones de servicio",
                "Evento sobre comisiones de servicio.",
                EventCategory.SINDICAL,
                Importance.MEDIUM,
                List.of(new AnalysisNewsItem(2L, "Titulo", "Resumen", longContent, OffsetDateTime.parse("2026-06-08T10:00:00Z"))),
                "",
                ""
        ));

        assertTrue(prompt.systemPrompt().contains("No mezcles idiomas"));
        assertTrue(prompt.userPrompt().contains("No repitas palabras o fragmentos"));
        assertTrue(prompt.userPrompt().contains("[recortado]"));
        assertFalse(prompt.userPrompt().contains(longContent));
        assertTrue(prompt.userPrompt().length() < 14_000);
    }
}
