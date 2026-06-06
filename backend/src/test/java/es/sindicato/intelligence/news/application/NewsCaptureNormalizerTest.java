package es.sindicato.intelligence.news.application;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class NewsCaptureNormalizerTest {

    @Test
    void normalizesTitleUrlSummaryAndContent() {
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        CreateNewsCommand normalized = normalizer.normalize(new CreateNewsCommand(
                2L,
                "  Convocatoria   docente  ",
                "  https://test.example/news/1  ",
                "  Resumen   con   espacios ",
                "  Contenido   con   espacios ",
                OffsetDateTime.parse("2026-06-06T09:00:00Z")
        ));

        assertEquals("Convocatoria docente", normalized.title());
        assertEquals("https://test.example/news/1", normalized.url());
        assertEquals("Resumen con espacios", normalized.summary());
        assertEquals("Contenido con espacios", normalized.content());
    }

    @Test
    void convertsBlankOptionalFieldsToNull() {
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        CreateNewsCommand normalized = normalizer.normalize(new CreateNewsCommand(
                2L,
                "Titulo",
                "https://test.example/news/1",
                "   ",
                "",
                null
        ));

        assertNull(normalized.summary());
        assertNull(normalized.content());
    }
}
