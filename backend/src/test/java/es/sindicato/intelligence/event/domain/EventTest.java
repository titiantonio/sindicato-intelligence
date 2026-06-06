package es.sindicato.intelligence.event.domain;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EventTest {

    @Test
    void createsEvent() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        Event event = event(now, Set.of(10L, 11L));

        assertEquals(1L, event.getId());
        assertEquals("Convocatoria Oposiciones Docentes Andalucia 2027", event.getTitle());
        assertEquals("Convocatoria acumulada desde varias noticias", event.getDescription());
        assertEquals(EventCategory.OPOSICIONES, event.getCategory());
        assertEquals(Importance.HIGH, event.getImportance());
        assertEquals(EventStatus.OPEN, event.getStatus());
        assertEquals(Set.of(10L, 11L), event.getNewsIds());
        assertEquals(now, event.getFirstDetectedAt());
        assertEquals(now, event.getLastUpdatedAt());
        assertEquals(now, event.getCreatedAt());
        assertEquals(now, event.getUpdatedAt());
    }

    @Test
    void rejectsMissingTitle() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new Event(
                1L,
                " ",
                "Descripcion",
                EventCategory.OPOSICIONES,
                Importance.HIGH,
                EventStatus.OPEN,
                Set.of(10L),
                now,
                now,
                now,
                now
        ));
    }

    @Test
    void rejectsEventWithoutNews() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> event(now, Set.of()));
    }

    @Test
    void rejectsLastUpdatedBeforeFirstDetected() {
        OffsetDateTime firstDetectedAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime lastUpdatedAt = OffsetDateTime.parse("2026-06-06T09:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new Event(
                1L,
                "Convocatoria Oposiciones Docentes Andalucia 2027",
                "Descripcion",
                EventCategory.OPOSICIONES,
                Importance.HIGH,
                EventStatus.OPEN,
                Set.of(10L),
                firstDetectedAt,
                lastUpdatedAt,
                firstDetectedAt,
                firstDetectedAt
        ));
    }

    @Test
    void rejectsUpdatedAtBeforeCreatedAt() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-06T09:00:00Z");

        assertThrows(IllegalArgumentException.class, () -> new Event(
                1L,
                "Convocatoria Oposiciones Docentes Andalucia 2027",
                "Descripcion",
                EventCategory.OPOSICIONES,
                Importance.HIGH,
                EventStatus.OPEN,
                Set.of(10L),
                createdAt,
                createdAt,
                createdAt,
                updatedAt
        ));
    }

    @Test
    void addsNewsToActiveEventAndUpdatesActivity() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-06T11:00:00Z");
        Event event = event(createdAt, Set.of(10L));

        event.addNews(11L, updatedAt);

        assertEquals(Set.of(10L, 11L), event.getNewsIds());
        assertEquals(updatedAt, event.getLastUpdatedAt());
        assertEquals(updatedAt, event.getUpdatedAt());
    }

    @Test
    void rejectsAddingNewsToClosedEvent() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime closedAt = OffsetDateTime.parse("2026-06-06T11:00:00Z");
        OffsetDateTime updatedAt = OffsetDateTime.parse("2026-06-06T12:00:00Z");
        Event event = event(createdAt, Set.of(10L));
        event.close(closedAt);

        assertThrows(IllegalStateException.class, () -> event.addNews(11L, updatedAt));
    }

    @Test
    void rejectsRemovingLastNews() {
        OffsetDateTime timestamp = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        Event event = event(timestamp, Set.of(10L));

        assertThrows(IllegalStateException.class, () -> event.removeNews(10L, timestamp));
    }

    @Test
    void changesStatusAndUpdatesActivity() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime monitoringAt = OffsetDateTime.parse("2026-06-06T11:00:00Z");
        Event event = event(createdAt, Set.of(10L));

        event.markMonitoring(monitoringAt);

        assertEquals(EventStatus.MONITORING, event.getStatus());
        assertTrue(event.isActive());
        assertEquals(monitoringAt, event.getLastUpdatedAt());
        assertEquals(monitoringAt, event.getUpdatedAt());
    }

    @Test
    void reopensClosedEvent() {
        OffsetDateTime createdAt = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        OffsetDateTime closedAt = OffsetDateTime.parse("2026-06-06T11:00:00Z");
        OffsetDateTime reopenedAt = OffsetDateTime.parse("2026-06-06T12:00:00Z");
        Event event = event(createdAt, Set.of(10L));
        event.close(closedAt);

        event.reopen(reopenedAt);

        assertEquals(EventStatus.OPEN, event.getStatus());
        assertTrue(event.isActive());
        assertEquals(reopenedAt, event.getLastUpdatedAt());
    }

    private Event event(OffsetDateTime timestamp, Set<Long> newsIds) {
        return new Event(
                1L,
                "Convocatoria Oposiciones Docentes Andalucia 2027",
                "Convocatoria acumulada desde varias noticias",
                EventCategory.OPOSICIONES,
                Importance.HIGH,
                EventStatus.OPEN,
                newsIds,
                timestamp,
                timestamp,
                timestamp,
                timestamp
        );
    }
}
