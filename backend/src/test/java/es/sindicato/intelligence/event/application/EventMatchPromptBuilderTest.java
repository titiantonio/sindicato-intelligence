package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import org.junit.jupiter.api.Test;

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
                        EventCategory.SIPRI
                ))
        );

        assertTrue(prompt.systemPrompt().contains("Eres un analista especializado en seguimiento informativo"));
        assertTrue(prompt.userPrompt().contains("NOTICIA NUEVA"));
        assertTrue(prompt.userPrompt().contains("EVENTOS EXISTENTES"));
        assertTrue(prompt.userPrompt().contains("Nueva adjudicacion SIPRI mayo 2026"));
        assertTrue(prompt.userPrompt().contains("\"eventId\": 123"));
        assertTrue(prompt.userPrompt().contains("\"confidence\": 95"));
    }
}
