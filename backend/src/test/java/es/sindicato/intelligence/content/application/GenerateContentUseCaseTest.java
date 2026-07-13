package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.ContentType;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventNewsAssociationTraceRepository;
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
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GenerateContentUseCaseTest {

    @Test
    void generatesAndPersistsContentUsingLatestAnalysis() {
        EventRepository eventRepository = mock(EventRepository.class);
        NewsRepository newsRepository = mock(NewsRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        ContentAIProvider aiProvider = mock(ContentAIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        RecordAuditLogUseCase audit = mock(RecordAuditLogUseCase.class);
        RelevantContentLinkExtractor linkExtractor = mock(RelevantContentLinkExtractor.class);
        CurrentContentAuthorProvider authorProvider = () -> 1L;
        GenerateContentUseCase useCase = new GenerateContentUseCase(eventRepository, newsRepository, analysisRepository, contentRepository, new GenerateContentPromptBuilder(), aiProvider, authorProvider, metricsRecorder, audit, linkExtractor, coordinator(), mock(EventNewsAssociationTraceRepository.class), new ContentAIResponseValidator());
        Event event = event();
        NewsArticle newsArticle = newsArticle(2L);
        EventAIAnalysis analysis = analysis(20L, event.getId());
        GeneratedContent savedContent = content(30L, event.getId(), 1L);
        List<RelevantContentLink> relevantLinks = List.of(new RelevantContentLink(2L, "Documento oficial", "https://www.juntadeandalucia.es/educacion/documento.pdf"));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(newsRepository.findById(2L)).thenReturn(Optional.of(newsArticle));
        when(analysisRepository.findByEventId(event.getId())).thenReturn(List.of(analysis));
        when(linkExtractor.extract(List.of(newsArticle))).thenReturn(relevantLinks);
        when(aiProvider.generate(any(ContentAIRequest.class))).thenReturn(aiResponse());
        when(aiProvider.providerName()).thenReturn("test-content-provider");
        when(aiProvider.modelName()).thenReturn("test-content-model");
        when(contentRepository.save(any(GeneratedContent.class))).thenReturn(savedContent);

        GeneratedContent result = useCase.execute(new GenerateContentCommand(event.getId(), null, "telegram", "informativo", null, "standard"));

        ArgumentCaptor<ContentAIRequest> requestCaptor = ArgumentCaptor.forClass(ContentAIRequest.class);
        ArgumentCaptor<GeneratedContent> contentCaptor = ArgumentCaptor.forClass(GeneratedContent.class);
        verify(aiProvider).generate(requestCaptor.capture());
        verify(contentRepository).save(contentCaptor.capture());
        verify(audit).record(eq("CONTENT_GENERATED"), eq("CONTENT"), eq(30L), isNull(), any());
        GeneratedContent contentToSave = contentCaptor.getValue();

        assertEquals(savedContent, result);
        assertEquals(event.getId(), requestCaptor.getValue().event().getId());
        assertEquals(analysis.getId(), requestCaptor.getValue().analysis().getId());
        assertEquals("TELEGRAM", requestCaptor.getValue().channel());
        assertEquals("INFORMATIVO", requestCaptor.getValue().tone());
        assertEquals(ContentType.TELEGRAM_POST, requestCaptor.getValue().contentType());
        assertEquals("STANDARD", requestCaptor.getValue().length());
        assertEquals(relevantLinks, requestCaptor.getValue().relevantLinks());
        assertEquals(true, requestCaptor.getValue().systemPrompt().contains("redactor de comunicacion institucional"));
        assertEquals(true, requestCaptor.getValue().userPrompt().contains("ANALISIS"));
        assertEquals(true, requestCaptor.getValue().userPrompt().contains("Documento oficial"));
        assertEquals(true, requestCaptor.getValue().userPrompt().contains("https://www.juntadeandalucia.es/educacion/documento.pdf"));
        assertEquals(event.getId(), contentToSave.getEventId());
        assertEquals(1L, contentToSave.getCreatedBy());
        assertEquals(ContentType.TELEGRAM_POST, contentToSave.getContentType());
        assertEquals("STANDARD", contentToSave.getLength());
        assertEquals(ContentStatus.PENDING_REVIEW, contentToSave.getStatus());
        assertEquals("Titulo Telegram", contentToSave.getTitle());
        assertEquals("Mensaje generado\n\n#Educacion #Andalucia", contentToSave.getContent());
        assertNotNull(contentToSave.getGeneratedAt());
        verify(metricsRecorder).recordSuccess(
                eq("CONTENT_GENERATION"),
                eq("WF05_CONTENT"),
                anyString(),
                eq("test-content-model"),
                eq("EVENT"),
                eq(event.getId()),
                isNull(),
                org.mockito.ArgumentMatchers.<Map<String, Object>>any()
        );
    }

    @Test
    void usesExplicitAnalysisWhenProvided() {
        EventRepository eventRepository = mock(EventRepository.class);
        NewsRepository newsRepository = mock(NewsRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        ContentAIProvider aiProvider = mock(ContentAIProvider.class);
        RelevantContentLinkExtractor linkExtractor = mock(RelevantContentLinkExtractor.class);
        GenerateContentUseCase useCase = new GenerateContentUseCase(eventRepository, newsRepository, analysisRepository, contentRepository, new GenerateContentPromptBuilder(), aiProvider, () -> 1L, mock(AiOperationMetricsRecorder.class), mock(RecordAuditLogUseCase.class), linkExtractor, coordinator(), mock(EventNewsAssociationTraceRepository.class), new ContentAIResponseValidator());
        Event event = event();
        NewsArticle newsArticle = newsArticle(2L);
        EventAIAnalysis analysis = analysis(20L, event.getId());

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(newsRepository.findById(2L)).thenReturn(Optional.of(newsArticle));
        when(analysisRepository.findById(analysis.getId())).thenReturn(Optional.of(analysis));
        when(linkExtractor.extract(List.of(newsArticle))).thenReturn(List.of());
        when(aiProvider.generate(any(ContentAIRequest.class))).thenReturn(aiResponse());
        when(contentRepository.save(any(GeneratedContent.class))).thenReturn(content(30L, event.getId(), 1L));

        useCase.execute(new GenerateContentCommand(event.getId(), analysis.getId(), null, null, null, null));

        verify(analysisRepository, never()).findByEventId(event.getId());
        verify(contentRepository).save(any(GeneratedContent.class));
    }

    @Test
    void rejectsEventWithoutAnalysis() {
        EventRepository eventRepository = mock(EventRepository.class);
        NewsRepository newsRepository = mock(NewsRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        GenerateContentUseCase useCase = new GenerateContentUseCase(eventRepository, newsRepository, analysisRepository, contentRepository, new GenerateContentPromptBuilder(), mock(ContentAIProvider.class), () -> 1L, mock(AiOperationMetricsRecorder.class), mock(RecordAuditLogUseCase.class), mock(RelevantContentLinkExtractor.class), coordinator(), mock(EventNewsAssociationTraceRepository.class), new ContentAIResponseValidator());
        Event event = event();

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(analysisRepository.findByEventId(event.getId())).thenReturn(List.of());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new GenerateContentCommand(event.getId(), null, "TELEGRAM", "INFORMATIVO", null, "STANDARD")));

        verify(contentRepository, never()).save(any(GeneratedContent.class));
    }

    @Test
    void rejectsAnalysisFromAnotherEvent() {
        EventRepository eventRepository = mock(EventRepository.class);
        NewsRepository newsRepository = mock(NewsRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        GenerateContentUseCase useCase = new GenerateContentUseCase(eventRepository, newsRepository, analysisRepository, contentRepository, new GenerateContentPromptBuilder(), mock(ContentAIProvider.class), () -> 1L, mock(AiOperationMetricsRecorder.class), mock(RecordAuditLogUseCase.class), mock(RelevantContentLinkExtractor.class), coordinator(), mock(EventNewsAssociationTraceRepository.class), new ContentAIResponseValidator());
        Event event = event();
        EventAIAnalysis analysis = analysis(20L, 999L);

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(analysisRepository.findById(analysis.getId())).thenReturn(Optional.of(analysis));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new GenerateContentCommand(event.getId(), analysis.getId(), "TELEGRAM", "INFORMATIVO", null, "STANDARD")));

        verify(contentRepository, never()).save(any(GeneratedContent.class));
    }

    @Test
    void rejectsOutdatedAnalysis() {
        EventRepository eventRepository = mock(EventRepository.class);
        NewsRepository newsRepository = mock(NewsRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        GenerateContentUseCase useCase = new GenerateContentUseCase(eventRepository, newsRepository, analysisRepository, contentRepository, new GenerateContentPromptBuilder(), mock(ContentAIProvider.class), () -> 1L, mock(AiOperationMetricsRecorder.class), mock(RecordAuditLogUseCase.class), mock(RelevantContentLinkExtractor.class), coordinator(), mock(EventNewsAssociationTraceRepository.class), new ContentAIResponseValidator());
        Event event = event();
        EventAIAnalysis analysis = analysis(20L, event.getId(), event.getUpdatedAt().minusMinutes(1));

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(analysisRepository.findById(analysis.getId())).thenReturn(Optional.of(analysis));

        assertThrows(IllegalStateException.class, () -> useCase.execute(new GenerateContentCommand(event.getId(), analysis.getId(), "TELEGRAM", "INFORMATIVO", null, "STANDARD")));

        verify(newsRepository, never()).findById(any());
        verify(contentRepository, never()).save(any(GeneratedContent.class));
    }

    @Test
    void rejectsActiveDuplicateContent() {
        EventRepository eventRepository = mock(EventRepository.class);
        NewsRepository newsRepository = mock(NewsRepository.class);
        EventAIAnalysisRepository analysisRepository = mock(EventAIAnalysisRepository.class);
        GeneratedContentRepository contentRepository = mock(GeneratedContentRepository.class);
        GenerateContentUseCase useCase = new GenerateContentUseCase(eventRepository, newsRepository, analysisRepository, contentRepository, new GenerateContentPromptBuilder(), mock(ContentAIProvider.class), () -> 1L, mock(AiOperationMetricsRecorder.class), mock(RecordAuditLogUseCase.class), mock(RelevantContentLinkExtractor.class), coordinator(), mock(EventNewsAssociationTraceRepository.class), new ContentAIResponseValidator());
        Event event = event();
        EventAIAnalysis analysis = analysis(20L, event.getId());

        when(eventRepository.findById(event.getId())).thenReturn(Optional.of(event));
        when(analysisRepository.findById(analysis.getId())).thenReturn(Optional.of(analysis));
        when(contentRepository.existsActiveByEventIdAndAnalysisIdAndChannelAndContentType(event.getId(), analysis.getId(), "TELEGRAM", ContentType.TELEGRAM_POST)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> useCase.execute(new GenerateContentCommand(event.getId(), analysis.getId(), "TELEGRAM", "INFORMATIVO", null, "STANDARD")));

        verify(newsRepository, never()).findById(any());
        verify(contentRepository, never()).save(any(GeneratedContent.class));
    }

    private ContentAIResponse aiResponse() {
        return new ContentAIResponse("Titulo Telegram", "Mensaje generado", List.of("#Educacion", "#Andalucia"));
    }

    private GeneratedContent content(Long id, Long eventId, Long createdBy) {
        return new GeneratedContent(
                id,
                eventId,
                createdBy,
                "TELEGRAM",
                "INFORMATIVO",
                "Titulo Telegram",
                "Mensaje generado\n\n#Educacion #Andalucia",
                ContentStatus.PENDING_REVIEW,
                OffsetDateTime.parse("2026-06-08T10:00:00Z"),
                null
        );
    }

    private EventAIAnalysis analysis(Long id, Long eventId) {
        return analysis(id, eventId, OffsetDateTime.parse("2026-06-08T10:00:00Z"));
    }

    private EventAIAnalysis analysis(Long id, Long eventId, OffsetDateTime eventUpdatedAtSnapshot) {
        return new EventAIAnalysis(
                id,
                eventId,
                "Resumen ejecutivo",
                "Resumen sindical",
                List.of("Punto clave"),
                List.of("Riesgo"),
                List.of("Oportunidad"),
                List.of("Profesorado"),
                List.of("Revisar BOJA"),
                es.sindicato.intelligence.analysis.domain.AnalysisType.STANDARD,
                es.sindicato.intelligence.analysis.domain.AnalysisGenerationTrigger.BATCH,
                eventUpdatedAtSnapshot,
                1,
                false,
                "deterministic-analysis",
                OffsetDateTime.parse("2026-06-08T10:00:00Z")
        );
    }

    private Event event() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new Event(10L, "Evento sindical", "Descripcion", EventCategory.SINDICAL, Importance.MEDIUM, EventStatus.OPEN, Set.of(2L), now, now, now, now);
    }

    private NewsArticle newsArticle(Long id) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-08T10:00:00Z");
        return new NewsArticle(id, 1L, "SIPRI publica adjudicaciones", "https://test.example/news", "Resumen", "Contenido", "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa", now.minusHours(1), now, NewsStatus.EVENT_MATCHED, now, now);
    }

    private AiModelExecutionCoordinator coordinator() {
        AiModelExecutionCoordinator coordinator = mock(AiModelExecutionCoordinator.class);
        org.mockito.Mockito.when(coordinator.execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.<java.util.function.Supplier<?>>getArgument(1).get());
        return coordinator;
    }
}
