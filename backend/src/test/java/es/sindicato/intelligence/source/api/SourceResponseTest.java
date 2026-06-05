package es.sindicato.intelligence.source.api;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SourceResponseTest {

    @Test
    void exposesSourceFields() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-06T11:00:00Z");

        SourceResponse response = new SourceResponse(
                1L,
                "BOJA",
                "https://www.juntadeandalucia.es/boja",
                "RSS",
                10,
                true,
                createdAt,
                updatedAt
        );

        assertEquals(1L, response.id());
        assertEquals("BOJA", response.name());
        assertEquals("https://www.juntadeandalucia.es/boja", response.url());
        assertEquals("RSS", response.type());
        assertEquals(10, response.priority());
        assertTrue(response.active());
        assertEquals(createdAt, response.createdAt());
        assertEquals(updatedAt, response.updatedAt());
    }
}
