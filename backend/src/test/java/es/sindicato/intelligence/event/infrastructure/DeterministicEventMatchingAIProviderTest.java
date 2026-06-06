package es.sindicato.intelligence.event.infrastructure;

import es.sindicato.intelligence.event.application.EventMatchCandidate;
import es.sindicato.intelligence.event.application.EventMatchingAIRequest;
import es.sindicato.intelligence.event.application.EventMatchingAIResponse;
import es.sindicato.intelligence.event.domain.EventCategory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeterministicEventMatchingAIProviderTest {

    private final DeterministicEventMatchingAIProvider provider = new DeterministicEventMatchingAIProvider();

    @Test
    void matchesSimilarEvent() {
        EventMatchingAIResponse response = provider.match(new EventMatchingAIRequest(
                "Nueva adjudicacion SIPRI mayo 2026",
                "La Consejeria publica la adjudicacion SIPRI de mayo.",
                "El procedimiento afecta a interinos docentes.",
                List.of(new EventMatchCandidate(
                        123L,
                        "Adjudicacion SIPRI mayo 2026",
                        "Evento sobre adjudicacion SIPRI de mayo",
                        EventCategory.SIPRI
                )),
                "system",
                "user"
        ));

        assertTrue(response.match());
        assertEquals(123L, response.eventId());
        assertTrue(response.confidence() >= 85);
    }

    @Test
    void returnsNoMatchWhenConfidenceIsLow() {
        EventMatchingAIResponse response = provider.match(new EventMatchingAIRequest(
                "Curso de formacion permanente para docentes",
                "Nueva actividad formativa provincial.",
                "Contenido de formacion",
                List.of(new EventMatchCandidate(
                        123L,
                        "Adjudicacion SIPRI mayo 2026",
                        "Evento sobre adjudicacion SIPRI",
                        EventCategory.SIPRI
                )),
                "system",
                "user"
        ));

        assertFalse(response.match());
        assertEquals(null, response.eventId());
        assertTrue(response.confidence() < 85);
    }
}
