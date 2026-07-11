package es.sindicato.intelligence.analysis.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenerateAnalysisUseCaseTest {

    @Test
    void generatesAndPersistsAnalysis() {
        EventRepository eventRepository = mock(EventRepository.class);
        NewsRepository newsRepository = mock(NewsRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        AnalysisAIProvider aiProvider = mock(AnalysisAIProvider.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        GenerateAnalysisUseCase useCase = new GenerateAnalysisUseCase(eventRepository, newsRepository, analysisRepository, new GenerateAnalysisPromptBuilder(), aiProvider, mock(AiOperationMetricsRecorder.class), audit, coordinator());
        Event event = event(Set.of(2L));
        NewsArticle newsArticle = newsArticle(2L);
        EventAIAnalysis savedAnalysis = analysis(20L, event.getId());

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(aiProvider.generate(any(AnalysisAIRequest.class))).thenReturn(aiResponse());
        when(analysisRepository.save(any(EventAIAnalysis.class))).thenReturn(savedAnalysis);

        EventAIAnalysis result = useCase.execute(new GenerateAnalysisCommand(event.getId()));

        ArgumentCaptor<AnalysisAIRequest> requestCaptor = ArgumentCaptor.forClass(AnalysisAIRequest.class);
        ArgumentCaptor<EventAIAnalysis> analysisCaptor = ArgumentCaptor.forClass(EventAIAnalysis.class);
        verify(aiProvider).generate(requestCaptor.capture());
        verify(analysisRepository).save(analysisCaptor.capture());
        verify(audit).record(eq("ANALYSIS_GENERATED"), eq("ANALYSIS"), eq(20L), isNull(), any());

        AnalysisAIRequest request = requestCaptor.getValue();
        EventAIAnalysis analysisToSave = analysisCaptor.getValue();
        assertEquals(savedAnalysis, result);
        assertEquals(event.getId(), request.eventId());
        assertEquals(event.getTitle(), request.eventTitle());
        assertEquals(1, request.news().size());
        assertEquals(newsArticle.getTitle(), request.news().getFirst().title());
        assertEquals(true, request.systemPrompt().contains("analista senior"));
        assertEquals(true, request.userPrompt().contains("executiveSummary"));
        assertEquals(event.getId(), analysisToSave.getEventId());
        assertEquals("Resumen ejecutivo", analysisToSave.getExecutiveSummary());
        assertEquals("Resumen sindical", analysisToSave.getUnionSummary());
        assertEquals(List.of("Punto clave"), analysisToSave.getKeyPoints());
        assertNotNull(analysisToSave.getGeneratedAt());
    }

    @Test
    void rejectsUnknownEvent() {
        EventRepository eventRepository = mock(EventRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GenerateAnalysisUseCase useCase = new GenerateAnalysisUseCase(eventRepository, mock(NewsRepository.class), analysisRepository, new GenerateAnalysisPromptBuilder(), mock(AnalysisAIProvider.class), mock(AiOperationMetricsRecorder.class), mock(RecordAuditLogUseCase.class), coordinator());

        when(eventRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new GenerateAnalysisCommand(1L)));

        verify(analysisRepository, never()).save(any(EventAIAnalysis.class));
    }

    @Test
    void rejectsEventWithMissingNews() {
        EventRepository eventRepository = mock(EventRepository.class);
        NewsRepository newsRepository = mock(NewsRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GenerateAnalysisUseCase useCase = new GenerateAnalysisUseCase(eventRepository, newsRepository, analysisRepository, new GenerateAnalysisPromptBuilder(), mock(AnalysisAIProvider.class), mock(AiOperationMetricsRecorder.class), mock(RecordAuditLogUseCase.class), coordinator());
        Event event = event(Set.of(2L));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(newsRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new GenerateAnalysisCommand(event.getId())));

        verify(analysisRepository, never()).save(any(EventAIAnalysis.class));
    }

    private AnalysisAIResponse aiResponse() {
        return new AnalysisAIResponse(
                "Resumen ejecutivo",
                "Resumen sindical",
                List.of("Punto clave"),
                List.of("Riesgo"),
                List.of("Oportunidad"),
                "deterministic-analysis"
        );
    }

    private EventAIAnalysis analysis(Long id, Long eventId) {
        return new EventAIAnalysis(
                id,
                eventId,
                "Resumen ejecutivo",
                "Resumen sindical",
                List.of("Punto clave"),
                List.of("Riesgo"),
                List.of("Oportunidad"),
                "deterministic-analysis",
                OffsetDateTime.parse("2026-06-08T10:00:00Z")
        );
    }

    private Event event(Set<Long> newsIds) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new Event(
                10L,
                "Movilizacion sindical 0-3",
                "Evento sobre movilizacion sindical en educacion infantil 0-3.",
                EventCategory.SINDICAL,
                Importance.MEDIUM,
                EventStatus.OPEN,
                newsIds,
                now,
                now,
                now,
                now
        );
    }

    private NewsArticle newsArticle(Long id) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new NewsArticle(
                id,
                1L,
                "CCOO proseguira la movilizacion hasta alcanzar mejoras para el 0-3",
                "https://test.example/news/0-3",
                "CCOO mantiene movilizaciones por mejoras.",
                "La informacion afecta al ciclo 0-3.",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                now.minusHours(1),
                now,
                NewsStatus.EVENT_MATCHED,
                now,
                now
        );
    }

    private AiModelExecutionCoordinator coordinator() {
        AiModelExecutionCoordinator coordinator = mock(AiModelExecutionCoordinator.class);
        org.mockito.Mockito.when(coordinator.execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.<java.util.function.Supplier<?>>getArgument(1).get());
        return coordinator;
    }
}
