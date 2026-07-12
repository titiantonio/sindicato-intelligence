package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

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
}
