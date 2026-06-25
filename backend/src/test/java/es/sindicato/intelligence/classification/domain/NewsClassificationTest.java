package es.sindicato.intelligence.classification.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class NewsClassificationTest {

    @Test
    void createsNewsClassification() {
        OffsetDateTime classifiedAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        NewsClassification classification = new NewsClassification(
                1L,
                2L,
                ClassificationCategory.SIPRI,
                "Adjudicaciones",
                BigDecimal.valueOf(92),
                ImpactLevel.HIGH,
                UrgencyLevel.MEDIUM,
                List.of("SIPRI", "interinos"),
                List.of("Junta de Andalucia"),
                classifiedAt
        );

        assertEquals(1L, classification.getId());
        assertEquals(2L, classification.getNewsId());
        assertEquals(ClassificationCategory.SIPRI, classification.getCategory());
        assertEquals("Adjudicaciones", classification.getSubcategory());
        assertEquals(BigDecimal.valueOf(92), classification.getRelevanceScore());
        assertEquals(ImpactLevel.HIGH, classification.getImpactLevel());
        assertEquals(UrgencyLevel.MEDIUM, classification.getUrgencyLevel());
        assertEquals(List.of("SIPRI", "interinos"), classification.getKeywords());
        assertEquals(List.of("Junta de Andalucia"), classification.getEntities());
        assertEquals(classifiedAt, classification.getClassifiedAt());
    }

    @Test
    void rejectsInvalidRelevanceScore() {
        OffsetDateTime classifiedAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new NewsClassification(
                1L,
                2L,
                ClassificationCategory.SIPRI,
                "Adjudicaciones",
                BigDecimal.valueOf(101),
                ImpactLevel.HIGH,
                UrgencyLevel.MEDIUM,
                List.of("SIPRI"),
                List.of("Junta de Andalucia"),
                classifiedAt
        ));
    }

    @Test
    void rejectsMissingNewsId() {
        OffsetDateTime classifiedAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        assertThrows(NullPointerException.class, () -> new NewsClassification(
                1L,
                null,
                ClassificationCategory.SIPRI,
                "Adjudicaciones",
                BigDecimal.valueOf(92),
                ImpactLevel.HIGH,
                UrgencyLevel.MEDIUM,
                List.of("SIPRI"),
                List.of("Junta de Andalucia"),
                classifiedAt
        ));
    }

    @Test
    void identifiesOnlyOfficialDiscardCategoriesAsDiscardableForEventDetection() {
        assertTrue(classification(ClassificationCategory.OTROS, "FUERA_DE_AMBITO", BigDecimal.ZERO).isDiscardableForEventDetection());
        assertTrue(classification(ClassificationCategory.OTROS, "INFORMACION_INSUFICIENTE", BigDecimal.ZERO).isDiscardableForEventDetection());
        assertFalse(classification(ClassificationCategory.OTROS, "Baja relevancia", BigDecimal.TEN).isDiscardableForEventDetection());
        assertFalse(classification(ClassificationCategory.UNIVERSIDAD, "Baja relevancia", BigDecimal.TEN).isDiscardableForEventDetection());
    }

    private NewsClassification classification(ClassificationCategory category, String subcategory, BigDecimal relevance) {
        return new NewsClassification(
                1L,
                2L,
                category,
                subcategory,
                relevance,
                ImpactLevel.LOW,
                UrgencyLevel.LOW,
                List.of(),
                List.of(),
                OffsetDateTime.parse("2026-06-06T10:00:00Z")
        );
    }
}
