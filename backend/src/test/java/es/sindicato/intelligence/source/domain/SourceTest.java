package es.sindicato.intelligence.source.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SourceTest {

    @Test
    void createsSourceWithAuditFields() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-05T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-05T11:00:00Z");

        Source source = new Source(
                1L,
                "BOJA",
                "https://www.juntadeandalucia.es/boja",
                "RSS",
                10,
                true,
                createdAt,
                updatedAt
        );

        assertEquals(1L, source.getId());
        assertEquals("BOJA", source.getName());
        assertEquals("https://www.juntadeandalucia.es/boja", source.getUrl());
        assertEquals("RSS", source.getType());
        assertEquals(10, source.getPriority());
        assertTrue(source.isActive());
        assertEquals(createdAt, source.getCreatedAt());
        assertEquals(updatedAt, source.getUpdatedAt());
    }

    @Test
    void activatesSourceAndUpdatesTimestamp() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-05T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-05T11:00:00Z");
        OffsetDateTime activatedAt = OffsetDateTime.parse("2026-06-05T12:00:00Z");
        Source source = new Source(1L, "BOJA", "https://example.com", "RSS", 10, false, createdAt, updatedAt);

        source.activate(activatedAt);

        assertTrue(source.isActive());
        assertEquals(activatedAt, source.getUpdatedAt());
    }

    @Test
    void deactivatesSourceAndUpdatesTimestamp() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-05T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-05T11:00:00Z");
        OffsetDateTime deactivatedAt = OffsetDateTime.parse("2026-06-05T12:00:00Z");
        Source source = new Source(1L, "BOJA", "https://example.com", "RSS", 10, true, createdAt, updatedAt);

        source.deactivate(deactivatedAt);

        assertFalse(source.isActive());
        assertEquals(deactivatedAt, source.getUpdatedAt());
    }

    @Test
    void rejectsUpdatedAtBeforeCreatedAt() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-05T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-05T09:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new Source(
                1L,
                "BOJA",
                "https://example.com",
                "RSS",
                10,
                true,
                createdAt,
                updatedAt
        ));
    }
}
