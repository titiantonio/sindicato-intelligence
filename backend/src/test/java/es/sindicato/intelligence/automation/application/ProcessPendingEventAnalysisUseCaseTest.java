package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.analysis.application.GenerateAnalysisCommand;
import es.sindicato.intelligence.analysis.application.GenerateAnalysisUseCase;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessPendingEventAnalysisUseCaseTest {

    @Test
    void processesOpenEventsWithoutAnalysis() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GenerateAnalysisUseCase generateAnalysisUseCase = mock(GenerateAnalysisUseCase.class);
        ProcessPendingEventAnalysisUseCase useCase = new ProcessPendingEventAnalysisUseCase(eventRepository, analysisRepository, generateAnalysisUseCase, 10);
        Event first = event(1L);
        Event second = event(2L);

        when(eventRepository.findByStatusIn(List.of(EventStatus.OPEN, EventStatus.MONITORING))).thenReturn(List.of(first, second));
        when(analysisRepository.existsByEventId(first.getId())).thenReturn(false);
        when(analysisRepository.existsByEventId(second.getId())).thenReturn(true);

        AutomationRunResult result = useCase.executePending();

        verify(generateAnalysisUseCase).execute(new GenerateAnalysisCommand(first.getId()));
        verify(generateAnalysisUseCase, never()).execute(new GenerateAnalysisCommand(second.getId()));
        assertEquals(1, result.processedCount());
        assertEquals(1, result.successCount());
        assertEquals(0, result.failedCount());
    }

    @Test
    void reportsManualEventFailure() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GenerateAnalysisUseCase generateAnalysisUseCase = mock(GenerateAnalysisUseCase.class);
        ProcessPendingEventAnalysisUseCase useCase = new ProcessPendingEventAnalysisUseCase(eventRepository, analysisRepository, generateAnalysisUseCase, 10);

        when(analysisRepository.existsByEventId(5L)).thenReturn(false);
        doThrow(new IllegalArgumentException("analysis failed")).when(generateAnalysisUseCase).execute(new GenerateAnalysisCommand(5L));

        AutomationRunResult result = useCase.execute(new RunPendingAnalysisCommand(5L));

        assertEquals(1, result.processedCount());
        assertEquals(0, result.successCount());
        assertEquals(1, result.failedCount());
        assertEquals(5L, result.errors().getFirst().entityId());
    }

    @Test
    void limitsPendingAnalysisBatch() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GenerateAnalysisUseCase generateAnalysisUseCase = mock(GenerateAnalysisUseCase.class);
        ProcessPendingEventAnalysisUseCase useCase = new ProcessPendingEventAnalysisUseCase(eventRepository, analysisRepository, generateAnalysisUseCase, 1);
        Event first = event(1L);
        Event second = event(2L);

        when(eventRepository.findByStatusIn(List.of(EventStatus.OPEN, EventStatus.MONITORING))).thenReturn(List.of(first, second));
        when(analysisRepository.existsByEventId(first.getId())).thenReturn(false);
        when(analysisRepository.existsByEventId(second.getId())).thenReturn(false);

        AutomationRunResult result = useCase.executePending();

        ArgumentCaptor<GenerateAnalysisCommand> commandCaptor = ArgumentCaptor.forClass(GenerateAnalysisCommand.class);
        verify(generateAnalysisUseCase, times(1)).execute(commandCaptor.capture());
        assertEquals(new GenerateAnalysisCommand(first.getId()), commandCaptor.getValue());
        assertEquals(1, result.processedCount());
    }

    private Event event(Long id) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-15T10:00:00Z");
        return new Event(
                id,
                "Evento " + id,
                "Descripcion",
                EventCategory.SIPRI,
                Importance.HIGH,
                EventStatus.OPEN,
                Set.of(id + 100),
                now,
                now,
                now,
                now
        );
    }
}
