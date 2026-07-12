package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.ai.application.AiModelExecutionCoordinator;
import es.sindicato.intelligence.ai.application.AiOperationMetricsRecorder;
import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventMatchDecision;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class DetectEventUseCaseTest {

    @Test
    void rejectsDiscardedNewsByStatus() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                mock(AiOperationMetricsRecorder.class),
                coordinator()
        );
        NewsArticle newsArticle = newsArticle(NewsStatus.DISCARDED);

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new DetectEventCommand(newsArticle.getId())));

        verify(classificationRepository, never()).findByNewsId(any());
        verify(eventRepository, never()).save(any(Event.class));
        verify(aiProvider, never()).match(any(EventMatchingAIRequest.class));
    }

    @Test
    void discardsClassifiedNewsWithOutOfScopeClassificationWithoutCreatingEvent() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                mock(AiOperationMetricsRecorder.class),
                coordinator()
        );
        NewsArticle newsArticle = newsArticle(NewsStatus.CLASSIFIED);

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(eventRepository.existsNewsAssociation(newsArticle.getId())).thenReturn(false);
        when(classificationRepository.findByNewsId(newsArticle.getId())).thenReturn(Optional.of(discardableClassification(newsArticle.getId())));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new DetectEventCommand(newsArticle.getId())));

        verify(newsRepository).save(newsArticle);
        verify(eventRepository, never()).findByStatusIn(any());
        verify(eventRepository, never()).save(any(Event.class));
        verify(eventRepository, never()).saveNewsAssociation(any(), any(), any());
        verify(aiProvider, never()).match(any(EventMatchingAIRequest.class));
        assertEquals(NewsStatus.DISCARDED, newsArticle.getProcessingStatus());
    }

    @Test
    void retriesWithReducedContextWhenGeminiReturnsNoText() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        AiOperationMetricsRecorder metricsRecorder = mock(AiOperationMetricsRecorder.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                metricsRecorder,
                coordinator()
        );
        NewsArticle newsArticle = newsArticle(
                NewsStatus.CLASSIFIED,
                "Educar en diversidad: 28 series que visibilizan los problemas LGTBI+",
                "Recursos para educar en diversidad y crear respeto en clase.",
                "Mas de la mitad del alumnado ha sufrido acoso o ciberacoso durante la Educacion Secundaria."
        );
        Event candidate = event(50L, "Los mejores cortometrajes de tematica LGTBI para adolescentes");

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(eventRepository.existsNewsAssociation(newsArticle.getId())).thenReturn(false);
        when(classificationRepository.findByNewsId(newsArticle.getId())).thenReturn(Optional.of(classification(newsArticle.getId())));
        when(eventRepository.findByStatusIn(any())).thenReturn(List.of(candidate));
        when(aiProvider.match(any(EventMatchingAIRequest.class)))
                .thenThrow(new IllegalArgumentException("Gemini response does not contain candidates[0].content.parts[0].text"))
                .thenReturn(new EventMatchingAIResponse(true, candidate.getId(), 90, "Misma tematica educativa LGTBI."));
        when(eventRepository.save(candidate)).thenReturn(candidate);
        when(aiProvider.providerName()).thenReturn("gemini");
        when(aiProvider.modelName()).thenReturn("models/gemma-4-26b-a4b-it");

        DetectEventResult result = useCase.execute(new DetectEventCommand(newsArticle.getId()));

        ArgumentCaptor<EventMatchingAIRequest> requestCaptor = ArgumentCaptor.forClass(EventMatchingAIRequest.class);
        verify(aiProvider, times(2)).match(requestCaptor.capture());
        assertEquals(candidate.getId(), result.eventId());
        assertEquals(NewsStatus.EVENT_MATCHED, newsArticle.getProcessingStatus());
        org.junit.jupiter.api.Assertions.assertTrue(requestCaptor.getAllValues().getFirst().newsContent().contains("acoso"));
        org.junit.jupiter.api.Assertions.assertFalse(requestCaptor.getAllValues().get(1).newsContent().contains("acoso"));
        org.junit.jupiter.api.Assertions.assertTrue(requestCaptor.getAllValues().get(1).newsContent().contains("Contexto reducido"));
        verify(eventRepository).saveNewsAssociation(candidate.getId(), newsArticle.getId(), 90, EventMatchDecision.AUTOMATIC_MATCH, "Misma tematica educativa LGTBI.");
    }

    @Test
    void verifiesDoubtfulMatchBeforeAssociatingExistingEvent() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                mock(AiOperationMetricsRecorder.class),
                coordinator()
        );
        NewsArticle newsArticle = newsArticle(NewsStatus.CLASSIFIED);
        Event candidate = event(60L, "Recursos de inclusion y diversidad en centros docentes");

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(newsRepository.findById(100L)).thenReturn(Optional.of(newsArticle(NewsStatus.EVENT_MATCHED, "Diversidad en centros docentes", "Resumen", "Contenido")));
        when(eventRepository.existsNewsAssociation(newsArticle.getId())).thenReturn(false);
        when(classificationRepository.findByNewsId(newsArticle.getId())).thenReturn(Optional.of(classification(newsArticle.getId())));
        when(eventRepository.findByStatusIn(any())).thenReturn(List.of(candidate));
        when(aiProvider.match(any(EventMatchingAIRequest.class)))
                .thenReturn(new EventMatchingAIResponse(true, candidate.getId(), 78, "Coincidencia posible."))
                .thenReturn(new EventMatchingAIResponse(true, candidate.getId(), 88, "Coincidencia confirmada."));
        when(eventRepository.save(candidate)).thenReturn(candidate);

        DetectEventResult result = useCase.execute(new DetectEventCommand(newsArticle.getId()));

        assertEquals(candidate.getId(), result.eventId());
        assertEquals(EventMatchDecision.VERIFIED_MATCH, result.matchDecision());
        verify(aiProvider, times(2)).match(any(EventMatchingAIRequest.class));
        verify(eventRepository).saveNewsAssociation(candidate.getId(), newsArticle.getId(), 88, EventMatchDecision.VERIFIED_MATCH, "Coincidencia confirmada.");
    }

    @Test
    void includesRelatedCategoryCandidateWhenTextIsRelevant() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                mock(AiOperationMetricsRecorder.class),
                coordinator()
        );
        NewsArticle newsArticle = newsArticle(
                NewsStatus.CLASSIFIED,
                "Nuevas medidas de diversidad en el curriculo escolar",
                "La Junta revisa recursos de inclusion y diversidad curricular.",
                "Centros docentes aplicaran materiales de inclusion en el curriculo."
        );
        Event related = event(61L, "Revision del curriculo para inclusion y diversidad", EventCategory.CURRICULO);

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(newsRepository.findById(100L)).thenReturn(Optional.of(newsArticle(NewsStatus.EVENT_MATCHED, "Curriculo e inclusion", "Resumen", "Contenido")));
        when(eventRepository.existsNewsAssociation(newsArticle.getId())).thenReturn(false);
        when(classificationRepository.findByNewsId(newsArticle.getId())).thenReturn(Optional.of(classification(newsArticle.getId())));
        when(eventRepository.findByStatusIn(any())).thenReturn(List.of(related));
        when(aiProvider.match(any(EventMatchingAIRequest.class)))
                .thenReturn(new EventMatchingAIResponse(true, related.getId(), 90, "Coincide con el evento curricular."));
        when(eventRepository.save(related)).thenReturn(related);

        DetectEventResult result = useCase.execute(new DetectEventCommand(newsArticle.getId()));

        ArgumentCaptor<EventMatchingAIRequest> requestCaptor = ArgumentCaptor.forClass(EventMatchingAIRequest.class);
        verify(aiProvider).match(requestCaptor.capture());
        assertEquals(related.getId(), result.eventId());
        assertEquals(EventMatchDecision.AUTOMATIC_MATCH, result.matchDecision());
        assertEquals(EventCategory.CURRICULO, requestCaptor.getValue().candidates().getFirst().category());
    }

    @Test
    void createsNewEventWithReviewRecommendedDecisionWhenDoubtPersists() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                mock(AiOperationMetricsRecorder.class),
                coordinator()
        );
        NewsArticle newsArticle = newsArticle(NewsStatus.CLASSIFIED);
        Event candidate = event(62L, "Recursos de inclusion ya existentes");
        Event createdEvent = event(77L, newsArticle.getTitle());

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(newsRepository.findById(100L)).thenReturn(Optional.of(newsArticle(NewsStatus.EVENT_MATCHED, "Inclusion previa", "Resumen", "Contenido")));
        when(eventRepository.existsNewsAssociation(newsArticle.getId())).thenReturn(false);
        when(classificationRepository.findByNewsId(newsArticle.getId())).thenReturn(Optional.of(classification(newsArticle.getId())));
        when(eventRepository.findByStatusIn(any())).thenReturn(List.of(candidate));
        when(aiProvider.match(any(EventMatchingAIRequest.class)))
                .thenReturn(new EventMatchingAIResponse(true, candidate.getId(), 76, "Coincidencia dudosa."))
                .thenReturn(new EventMatchingAIResponse(false, null, 64, "No se confirma la coincidencia."));
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);

        DetectEventResult result = useCase.execute(new DetectEventCommand(newsArticle.getId()));

        assertEquals(createdEvent.getId(), result.eventId());
        assertEquals(EventMatchDecision.REVIEW_RECOMMENDED_NEW_EVENT, result.matchDecision());
        verify(aiProvider, times(2)).match(any(EventMatchingAIRequest.class));
        verify(eventRepository).saveNewsAssociation(createdEvent.getId(), newsArticle.getId(), 64, EventMatchDecision.REVIEW_RECOMMENDED_NEW_EVENT, "No se confirma la coincidencia.");
    }

    @Test
    void rejectsAiSuggestedEventIdThatWasNotSentAsCandidate() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        NewsClassificationRepository classificationRepository = mock(NewsClassificationRepository.class);
        EventRepository eventRepository = mock(EventRepository.class);
        EventMatchingAIProvider aiProvider = mock(EventMatchingAIProvider.class);
        DetectEventUseCase useCase = new DetectEventUseCase(
                newsRepository,
                classificationRepository,
                eventRepository,
                new EventMatchPromptBuilder(),
                aiProvider,
                mock(AiOperationMetricsRecorder.class),
                coordinator()
        );
        NewsArticle newsArticle = newsArticle(NewsStatus.CLASSIFIED);
        Event candidate = event(62L, "Recursos de inclusion ya existentes");
        Event notSentCandidate = event(999L, "Evento activo no enviado a la IA", EventCategory.OTROS);
        Event createdEvent = event(78L, newsArticle.getTitle());

        when(newsRepository.findById(newsArticle.getId())).thenReturn(Optional.of(newsArticle));
        when(newsRepository.findById(100L)).thenReturn(Optional.of(newsArticle(NewsStatus.EVENT_MATCHED, "Inclusion previa", "Resumen", "Contenido")));
        when(eventRepository.existsNewsAssociation(newsArticle.getId())).thenReturn(false);
        when(classificationRepository.findByNewsId(newsArticle.getId())).thenReturn(Optional.of(classification(newsArticle.getId())));
        when(eventRepository.findByStatusIn(any())).thenReturn(List.of(candidate, notSentCandidate));
        when(aiProvider.match(any(EventMatchingAIRequest.class)))
                .thenReturn(new EventMatchingAIResponse(true, notSentCandidate.getId(), 95, "Sugiere evento no enviado."));
        when(eventRepository.save(any(Event.class))).thenReturn(createdEvent);

        DetectEventResult result = useCase.execute(new DetectEventCommand(newsArticle.getId()));

        assertEquals(createdEvent.getId(), result.eventId());
        assertEquals(EventMatchDecision.REVIEW_RECOMMENDED_NEW_EVENT, result.matchDecision());
        verify(eventRepository).saveNewsAssociation(createdEvent.getId(), newsArticle.getId(), 95, EventMatchDecision.REVIEW_RECOMMENDED_NEW_EVENT, "Sugiere evento no enviado.");
    }

    private NewsArticle newsArticle(NewsStatus status) {
        return newsArticle(status, "Noticia fuera de ambito", "Resumen", "Contenido");
    }

    private NewsArticle newsArticle(NewsStatus status, String title, String summary, String content) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-15T10:00:00Z");
        return new NewsArticle(
                2L,
                1L,
                title,
                "https://test.example/news/out-of-scope",
                summary,
                content,
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                now.minusHours(1),
                now,
                status,
                now,
                now
        );
    }

    private NewsClassification classification(Long newsId) {
        return new NewsClassification(
                11L,
                newsId,
                ClassificationCategory.INCLUSION,
                "Educacion LGTBI+",
                BigDecimal.valueOf(20),
                ImpactLevel.LOW,
                UrgencyLevel.LOW,
                List.of("diversidad"),
                List.of(),
                OffsetDateTime.parse("2026-06-15T10:00:00Z")
        );
    }

    private Event event(Long id, String title) {
        return event(id, title, EventCategory.INCLUSION);
    }

    private Event event(Long id, String title, EventCategory category) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-15T10:00:00Z");
        return new Event(
                id,
                title,
                "Recursos educativos para trabajar diversidad y respeto con adolescentes.",
                category,
                Importance.LOW,
                EventStatus.OPEN,
                Set.of(100L),
                now.minusDays(1),
                now.minusDays(1),
                now.minusDays(1),
                now.minusDays(1)
        );
    }

    private NewsClassification discardableClassification(Long newsId) {
        return new NewsClassification(
                10L,
                newsId,
                ClassificationCategory.OTROS,
                "FUERA_DE_AMBITO",
                BigDecimal.ZERO,
                ImpactLevel.LOW,
                UrgencyLevel.LOW,
                List.of(),
                List.of(),
                OffsetDateTime.parse("2026-06-15T10:00:00Z")
        );
    }

    private AiModelExecutionCoordinator coordinator() {
        AiModelExecutionCoordinator coordinator = mock(AiModelExecutionCoordinator.class);
        org.mockito.Mockito.when(coordinator.execute(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any()))
                .thenAnswer(invocation -> invocation.<java.util.function.Supplier<?>>getArgument(1).get());
        return coordinator;
    }
}
