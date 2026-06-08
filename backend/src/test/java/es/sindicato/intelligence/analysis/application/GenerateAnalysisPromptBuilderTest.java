package es.sindicato.intelligence.analysis.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

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
}
