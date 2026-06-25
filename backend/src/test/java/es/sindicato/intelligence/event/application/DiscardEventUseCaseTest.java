package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DiscardEventUseCaseTest {

    @Test
    void archivesActiveEventAndRecordsAudit() {
        EventRepository eventRepository = mock(EventRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        DiscardEventUseCase useCase = new DiscardEventUseCase(eventRepository, audit);
        Event event = event(EventStatus.OPEN);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = useCase.execute(1L);

        assertEquals(EventStatus.ARCHIVED, result.getStatus());
        verify(eventRepository).save(event);
        verify(audit).record(eq("EVENT_DISCARDED"), eq("EVENT"), eq(1L), any(), any());
    }

    @Test
    void rejectsClosedEvents() {
        EventRepository eventRepository = mock(EventRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        DiscardEventUseCase useCase = new DiscardEventUseCase(eventRepository, audit);

        when(eventRepository.findById(1L)).thenReturn(Optional.of(event(EventStatus.CLOSED)));

        assertThrows(IllegalStateException.class, () -> useCase.execute(1L));
    }

    private Event event(EventStatus status) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-25T10:00:00Z");
        return new Event(
                1L,
                "Evento a descartar",
                "Descripcion",
                EventCategory.SIPRI,
                Importance.HIGH,
                status,
                Set.of(10L),
                now,
                now,
                now,
                now
        );
    }
}
