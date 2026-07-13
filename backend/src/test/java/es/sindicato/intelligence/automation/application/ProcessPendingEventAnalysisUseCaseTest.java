package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.analysis.application.GenerateAnalysisCommand;
import es.sindicato.intelligence.analysis.application.GenerateAnalysisUseCase;
import es.sindicato.intelligence.analysis.domain.AnalysisGenerationTrigger;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
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
import java.util.Optional;
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
        when(analysisRepository.findLatestByEventId(first.getId())).thenReturn(Optional.empty());
        when(analysisRepository.findLatestByEventId(second.getId())).thenReturn(Optional.of(analysis(second)));

        AutomationRunResult result = useCase.executePending();

        verify(generateAnalysisUseCase).execute(new GenerateAnalysisCommand(first.getId(), AnalysisGenerationTrigger.PRIORITY_AUTO));
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

        Event event = event(5L);
        when(eventRepository.findById(5L)).thenReturn(Optional.of(event));
        when(analysisRepository.findLatestByEventId(5L)).thenReturn(Optional.empty());
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
        when(analysisRepository.findLatestByEventId(first.getId())).thenReturn(Optional.empty());
        when(analysisRepository.findLatestByEventId(second.getId())).thenReturn(Optional.empty());

        AutomationRunResult result = useCase.executePending();

        ArgumentCaptor<GenerateAnalysisCommand> commandCaptor = ArgumentCaptor.forClass(GenerateAnalysisCommand.class);
        verify(generateAnalysisUseCase, times(1)).execute(commandCaptor.capture());
        assertEquals(new GenerateAnalysisCommand(second.getId(), AnalysisGenerationTrigger.PRIORITY_AUTO), commandCaptor.getValue());
        assertEquals(1, result.processedCount());
    }

    @Test
    void prioritizesCriticalEventsBeforeHighAndMedium() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GenerateAnalysisUseCase generateAnalysisUseCase = mock(GenerateAnalysisUseCase.class);
        ProcessPendingEventAnalysisUseCase useCase = new ProcessPendingEventAnalysisUseCase(eventRepository, analysisRepository, generateAnalysisUseCase, 2);
        Event medium = event(1L, Importance.MEDIUM, 1);
        Event critical = event(2L, Importance.CRITICAL, 1);
        Event high = event(3L, Importance.HIGH, 1);

        when(eventRepository.findByStatusIn(List.of(EventStatus.OPEN, EventStatus.MONITORING))).thenReturn(List.of(medium, high, critical));
        when(analysisRepository.findLatestByEventId(medium.getId())).thenReturn(Optional.empty());
        when(analysisRepository.findLatestByEventId(high.getId())).thenReturn(Optional.empty());
        when(analysisRepository.findLatestByEventId(critical.getId())).thenReturn(Optional.empty());

        AutomationRunResult result = useCase.executePending(2);

        ArgumentCaptor<GenerateAnalysisCommand> commandCaptor = ArgumentCaptor.forClass(GenerateAnalysisCommand.class);
        verify(generateAnalysisUseCase, times(2)).execute(commandCaptor.capture());
        assertEquals(List.of(
                new GenerateAnalysisCommand(critical.getId(), AnalysisGenerationTrigger.PRIORITY_AUTO),
                new GenerateAnalysisCommand(high.getId(), AnalysisGenerationTrigger.PRIORITY_AUTO)
        ), commandCaptor.getAllValues());
        assertEquals(2, result.processedCount());
    }

    @Test
    void skipsLowImportanceEventsWithoutEnoughNews() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GenerateAnalysisUseCase generateAnalysisUseCase = mock(GenerateAnalysisUseCase.class);
        ProcessPendingEventAnalysisUseCase useCase = new ProcessPendingEventAnalysisUseCase(eventRepository, analysisRepository, generateAnalysisUseCase, 10);
        Event low = event(1L, Importance.LOW, 2);

        when(eventRepository.findByStatusIn(List.of(EventStatus.OPEN, EventStatus.MONITORING))).thenReturn(List.of(low));
        when(analysisRepository.findLatestByEventId(low.getId())).thenReturn(Optional.empty());

        AutomationRunResult result = useCase.executePending();

        verify(generateAnalysisUseCase, never()).execute(new GenerateAnalysisCommand(low.getId()));
        assertEquals(0, result.processedCount());
    }

    private Event event(Long id) {
        return event(id, Importance.HIGH, 1);
    }

    private Event event(Long id, Importance importance, int newsCount) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-15T10:00:00Z");
        Set<Long> newsIds = java.util.stream.LongStream.range(0, newsCount)
                .mapToObj(offset -> id + 100 + offset)
                .collect(java.util.stream.Collectors.toSet());
        return new Event(
                id,
                "Evento " + id,
                "Descripcion",
                EventCategory.SIPRI,
                importance,
                EventStatus.OPEN,
                newsIds,
                now,
                now,
                now,
                now
        );
    }

    private EventAIAnalysis analysis(Event event) {
        return new EventAIAnalysis(
                20L,
                event.getId(),
                "Resumen",
                "Resumen sindical",
                List.of("Punto"),
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                es.sindicato.intelligence.analysis.domain.AnalysisType.PRIORITY,
                AnalysisGenerationTrigger.PRIORITY_AUTO,
                event.getUpdatedAt(),
                event.getNewsIds().size(),
                false,
                "deterministic-analysis",
                event.getUpdatedAt()
        );
    }
}
