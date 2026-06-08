package es.sindicato.intelligence.analysis.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventAIAnalysisTest {

    @Test
    void createsAnalysis() {
        OffsetDateTime generatedAt = OffsetDateTime.parse("2026-06-08T10:00:00Z");

        EventAIAnalysis analysis = new EventAIAnalysis(
                1L,
                10L,
                "Resumen ejecutivo",
                "Resumen sindical",
                List.of("Punto clave"),
                List.of("Riesgo"),
                List.of("Oportunidad"),
                "deterministic-analysis",
                generatedAt
        );

        assertEquals(1L, analysis.getId());
        assertEquals(10L, analysis.getEventId());
        assertEquals("Resumen ejecutivo", analysis.getExecutiveSummary());
        assertEquals("Resumen sindical", analysis.getUnionSummary());
        assertEquals(List.of("Punto clave"), analysis.getKeyPoints());
        assertEquals(List.of("Riesgo"), analysis.getRisks());
        assertEquals(List.of("Oportunidad"), analysis.getOpportunities());
        assertEquals("deterministic-analysis", analysis.getModelUsed());
        assertEquals(generatedAt, analysis.getGeneratedAt());
    }

    @Test
    void rejectsMissingEventId() {
        assertThrows(NullPointerException.class, () -> analysis(null, "Resumen ejecutivo", "Resumen sindical"));
    }

    @Test
    void rejectsMissingExecutiveSummary() {
        assertThrows(IllegalArgumentException.class, () -> analysis(10L, " ", "Resumen sindical"));
    }

    @Test
    void rejectsMissingUnionSummary() {
        assertThrows(IllegalArgumentException.class, () -> analysis(10L, "Resumen ejecutivo", null));
    }

    @Test
    void protectsCollectionsFromExternalMutation() {
        EventAIAnalysis analysis = analysis(10L, "Resumen ejecutivo", "Resumen sindical");

        assertThrows(UnsupportedOperationException.class, () -> analysis.getKeyPoints().add("Otro punto"));
        assertTrue(analysis.getKeyPoints().contains("Punto clave"));
    }

    private EventAIAnalysis analysis(Long eventId, String executiveSummary, String unionSummary) {
        return new EventAIAnalysis(
                null,
                eventId,
                executiveSummary,
                unionSummary,
                List.of("Punto clave"),
                List.of("Riesgo"),
                List.of("Oportunidad"),
                "deterministic-analysis",
                OffsetDateTime.parse("2026-06-08T10:00:00Z")
        );
    }
}
