package es.sindicato.intelligence.content.infrastructure;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.content.application.ContentAIRequest;
import es.sindicato.intelligence.content.application.ContentAIResponse;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DeterministicContentAIProviderTest {

    @Test
    void generatesDeterministicContent() {
        DeterministicContentAIProvider provider = new DeterministicContentAIProvider();

        ContentAIResponse response = provider.generate(new ContentAIRequest(event(), analysis(), "TELEGRAM", "INFORMATIVO", "STANDARD", List.of(), "system", "user"));

        assertEquals("Evento sindical", response.title());
        assertFalse(response.message().isBlank());
        assertEquals(List.of("#EducacionPublica", "#Andalucia"), response.hashtags());
    }

    private Event event() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new Event(10L, "Evento sindical", "Descripcion", EventCategory.SINDICAL, Importance.MEDIUM, EventStatus.OPEN, Set.of(2L), now, now, now, now);
    }

    private EventAIAnalysis analysis() {
        return new EventAIAnalysis(20L, 10L, "Resumen ejecutivo", "Resumen sindical", List.of("Punto clave"), List.of("Riesgo"), List.of("Oportunidad"), "deterministic-analysis", OffsetDateTime.parse("2026-06-08T10:00:00Z"));
    }
}
