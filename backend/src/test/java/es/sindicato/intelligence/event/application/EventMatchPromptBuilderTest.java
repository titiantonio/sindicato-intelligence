package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventMatchPromptBuilderTest {

    private final EventMatchPromptBuilder promptBuilder = new EventMatchPromptBuilder();

    @Test
    void buildsOfficialWf03Prompt() {
        EventMatchPrompt prompt = promptBuilder.build(
                "Nueva adjudicacion SIPRI mayo 2026",
                "La Consejeria publica una adjudicacion SIPRI.",
                "Contenido de la noticia",
                List.of(new EventMatchCandidate(
                        123L,
                        "Adjudicacion SIPRI mayo 2026",
                        "Evento sobre adjudicacion SIPRI",
                        EventCategory.SIPRI,
                        EventStatus.MONITORING,
                        OffsetDateTime.parse("2026-05-10T09:00:00Z"),
                        OffsetDateTime.parse("2026-05-12T09:00:00Z"),
                        2,
                        List.of("Primera adjudicacion SIPRI", "Nueva adjudicacion SIPRI")
                ))
        );

        assertTrue(prompt.systemPrompt().contains("Eres un analista especializado en seguimiento informativo"));
        assertTrue(prompt.userPrompt().contains("NOTICIA NUEVA"));
        assertTrue(prompt.userPrompt().contains("EVENTOS EXISTENTES"));
        assertTrue(prompt.userPrompt().contains("Nueva adjudicacion SIPRI mayo 2026"));
        assertTrue(prompt.userPrompt().contains("\"eventId\": 123"));
        assertTrue(prompt.userPrompt().contains("\"status\": \"MONITORING\""));
        assertTrue(prompt.userPrompt().contains("\"newsCount\": 2"));
        assertTrue(prompt.userPrompt().contains("Primera adjudicacion SIPRI"));
        assertTrue(prompt.userPrompt().contains("\"confidence\": 95"));
    }

    @Test
    void limitsLongNewsAndCandidateContextForGeminiMatching() {
        String longSummary = "Resumen largo ".repeat(500);
        String longContent = "Contenido largo ".repeat(900);
        String longCandidateDescription = "Descripcion larga del evento ".repeat(120);
        String longRecentTitle = "Titulo reciente largo ".repeat(40);

        EventMatchPrompt prompt = promptBuilder.build(
                "Titulo ".repeat(80),
                longSummary,
                longContent,
                List.of(new EventMatchCandidate(
                        456L,
                        "Evento candidato ".repeat(40),
                        longCandidateDescription,
                        EventCategory.FORMACION,
                        EventStatus.OPEN,
                        OffsetDateTime.parse("2026-07-01T09:00:00Z"),
                        OffsetDateTime.parse("2026-07-02T09:00:00Z"),
                        3,
                        List.of(longRecentTitle)
                ))
        );

        assertFalse(prompt.userPrompt().contains(longSummary));
        assertFalse(prompt.userPrompt().contains(longContent));
        assertFalse(prompt.userPrompt().contains(longCandidateDescription));
        assertFalse(prompt.userPrompt().contains(longRecentTitle));
        assertTrue(prompt.userPrompt().contains("[recortado]"));
        assertTrue(prompt.userPrompt().length() < 6_500);
    }

    @Test
    void omitsContentWhenItDuplicatesSummary() {
        String duplicatedContext = "Texto capturado repetido en resumen y contenido. ".repeat(100);

        EventMatchPrompt prompt = promptBuilder.build(
                "Master oficial en linea",
                duplicatedContext,
                duplicatedContext,
                List.of()
        );

        assertTrue(prompt.userPrompt().contains("[omitido: coincide con el resumen]"));
        assertFalse(prompt.userPrompt().contains("CONTENIDO:\n" + duplicatedContext));
    }
}
