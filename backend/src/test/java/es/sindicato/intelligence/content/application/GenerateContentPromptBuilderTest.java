package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class GenerateContentPromptBuilderTest {

    @Test
    void buildsOfficialWf05Prompt() {
        GenerateContentPromptBuilder builder = new GenerateContentPromptBuilder();

        GenerateContentPrompt prompt = builder.build(new ContentAIRequest(
                event(),
                analysis(),
                "TELEGRAM",
                "INFORMATIVO",
                "STANDARD",
                List.of(new RelevantContentLink(2L, "Consulta oficial", "https://www.juntadeandalucia.es/educacion/consulta")),
                "",
                ""
        ));

        assertTrue(prompt.systemPrompt().contains("redactor de comunicacion institucional"));
        assertTrue(prompt.systemPrompt().contains("No exageres"));
        assertTrue(prompt.userPrompt().contains("EVENTO"));
        assertTrue(prompt.userPrompt().contains("ANALISIS"));
        assertTrue(prompt.userPrompt().contains("ENLACES RELEVANTES PERMITIDOS"));
        assertTrue(prompt.userPrompt().contains("Consulta oficial"));
        assertTrue(prompt.userPrompt().contains("https://www.juntadeandalucia.es/educacion/consulta"));
        assertTrue(prompt.userPrompt().contains("title"));
        assertTrue(prompt.userPrompt().contains("hashtags"));
        assertTrue(prompt.userPrompt().contains("Longitud STANDARD"));
    }

    private Event event() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new Event(10L, "Evento sindical", "Descripcion", EventCategory.SINDICAL, Importance.MEDIUM, EventStatus.OPEN, Set.of(2L), now, now, now, now);
    }

    private EventAIAnalysis analysis() {
        return new EventAIAnalysis(20L, 10L, "Resumen ejecutivo", "Resumen sindical", List.of("Punto clave"), List.of("Riesgo"), List.of("Oportunidad"), "deterministic-analysis", OffsetDateTime.parse("2026-06-08T10:00:00Z"));
    }
}
