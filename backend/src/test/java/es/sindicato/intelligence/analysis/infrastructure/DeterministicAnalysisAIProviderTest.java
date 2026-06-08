package es.sindicato.intelligence.analysis.infrastructure;

import es.sindicato.intelligence.analysis.application.AnalysisAIRequest;
import es.sindicato.intelligence.analysis.application.AnalysisAIResponse;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class DeterministicAnalysisAIProviderTest {

    @Test
    void generatesDeterministicAnalysis() {
        DeterministicAnalysisAIProvider provider = new DeterministicAnalysisAIProvider();

        AnalysisAIResponse response = provider.generate(new AnalysisAIRequest(
                10L,
                "Evento",
                "Descripcion",
                EventCategory.SINDICAL,
                Importance.MEDIUM,
                List.of(),
                "system",
                "user"
        ));

        assertEquals("deterministic-analysis", response.modelUsed());
        assertFalse(response.executiveSummary().isBlank());
        assertFalse(response.keyPoints().isEmpty());
    }
}
