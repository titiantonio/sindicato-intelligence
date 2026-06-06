package es.sindicato.intelligence.classification.infrastructure;

import es.sindicato.intelligence.classification.application.ClassificationAIRequest;
import es.sindicato.intelligence.classification.application.ClassificationAIResponse;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DeterministicAIProviderTest {

    @Test
    void classifiesSipriNews() {
        DeterministicAIProvider provider = new DeterministicAIProvider();

        ClassificationAIResponse response = provider.classify(new ClassificationAIRequest(
                "SIPRI publica nuevas adjudicaciones",
                "Resumen",
                "Contenido",
                "system",
                "user"
        ));

        assertEquals(ClassificationCategory.SIPRI, response.category());
        assertEquals(ImpactLevel.HIGH, response.impact());
        assertEquals(UrgencyLevel.HIGH, response.urgency());
    }

    @Test
    void classifiesOposicionesNews() {
        DeterministicAIProvider provider = new DeterministicAIProvider();

        ClassificationAIResponse response = provider.classify(new ClassificationAIRequest(
                "Convocatoria de oposiciones docentes",
                "Resumen",
                "Contenido",
                "system",
                "user"
        ));

        assertEquals(ClassificationCategory.OPOSICIONES, response.category());
    }

    @Test
    void classifiesRetribucionesNewsWithAccents() {
        DeterministicAIProvider provider = new DeterministicAIProvider();

        ClassificationAIResponse response = provider.classify(new ClassificationAIRequest(
                "Actualizacion de nóminas docentes",
                "Resumen",
                "Contenido",
                "system",
                "user"
        ));

        assertEquals(ClassificationCategory.RETRIBUCIONES, response.category());
    }

    @Test
    void classifiesUnknownNewsAsOther() {
        DeterministicAIProvider provider = new DeterministicAIProvider();

        ClassificationAIResponse response = provider.classify(new ClassificationAIRequest(
                "Nueva noticia educativa",
                "Resumen",
                "Contenido",
                "system",
                "user"
        ));

        assertEquals(ClassificationCategory.OTROS, response.category());
    }
}
