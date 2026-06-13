package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class MergeEventsUseCaseTest {

    @Test
    void mergesSourceNewsIntoTargetAndArchivesSources() {
        EventRepository eventRepository = mock(EventRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        MergeEventsUseCase useCase = new MergeEventsUseCase(eventRepository, audit);
        Event target = event(1L, EventStatus.OPEN, Set.of(10L));
        Event source = event(2L, EventStatus.MONITORING, Set.of(11L, 12L));

        when(eventRepository.findById(1L)).thenReturn(Optional.of(target));
        when(eventRepository.findById(2L)).thenReturn(Optional.of(source));
        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = useCase.execute(new MergeEventsCommand(1L, List.of(2L)));

        assertEquals(Set.of(10L, 11L, 12L), result.getNewsIds());
        ArgumentCaptor<Event> savedEvents = ArgumentCaptor.forClass(Event.class);
        verify(eventRepository, times(2)).save(savedEvents.capture());
        assertEquals(EventStatus.ARCHIVED, savedEvents.getAllValues().get(1).getStatus());
        assertEquals(Set.of(), savedEvents.getAllValues().get(1).getNewsIds());
        verify(audit).record(eq("EVENT_MERGED"), eq("EVENT"), eq(1L), any(), any());
    }

    @Test
    void rejectsTargetAsSource() {
        EventRepository eventRepository = mock(EventRepository.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        MergeEventsUseCase useCase = new MergeEventsUseCase(eventRepository, audit);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new MergeEventsCommand(1L, List.of(1L))));
    }

    private Event event(Long id, EventStatus status, Set<Long> newsIds) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-10T10:00:00Z");
        return new Event(
                id,
                "Evento " + id,
                "Descripcion",
                EventCategory.OPOSICIONES,
                Importance.HIGH,
                status,
                newsIds,
                now,
                now,
                now,
                now
        );
    }
}
