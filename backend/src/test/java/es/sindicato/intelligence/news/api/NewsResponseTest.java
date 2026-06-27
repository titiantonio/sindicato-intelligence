package es.sindicato.intelligence.news.api;

import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class NewsResponseTest {

    @Test
    void exposesNewsFields() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-06T11:00:00Z");
        OffsetDateTime publishedAt = OffsetDateTime.parse("2026-06-06T09:00:00Z");
        OffsetDateTime capturedAt = OffsetDateTime.parse("2026-06-06T10:30:00Z");

        NewsResponse response = new NewsResponse(
                1L,
                2L,
                "Convocatoria docente",
                "https://test.example/news/1",
                "Resumen",
                "Contenido",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                publishedAt,
                capturedAt,
                NewsStatus.CAPTURED,
                createdAt,
                updatedAt
        );

        assertEquals(1L, response.id());
        assertEquals(2L, response.sourceId());
        assertEquals(null, response.sourceName());
        assertEquals("Convocatoria docente", response.title());
        assertEquals("https://test.example/news/1", response.url());
        assertEquals("Resumen", response.summary());
        assertEquals("Contenido", response.content());
        assertEquals("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", response.hash());
        assertEquals(publishedAt, response.publishedAt());
        assertEquals(capturedAt, response.capturedAt());
        assertEquals(NewsStatus.CAPTURED, response.processingStatus());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }
}
