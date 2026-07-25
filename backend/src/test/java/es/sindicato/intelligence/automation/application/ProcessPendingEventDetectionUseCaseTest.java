package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import es.sindicato.intelligence.event.application.DetectEventCommand;
import es.sindicato.intelligence.event.application.DetectEventUseCase;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessPendingEventDetectionUseCaseTest {

    @Test
    void processesClassifiedNewsAndReportsFailures() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        DetectEventUseCase detectEventUseCase = mock(DetectEventUseCase.class);
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        ProcessPendingEventDetectionUseCase useCase = new ProcessPendingEventDetectionUseCase(newsRepository, detectEventUseCase, metricRepository, 10, 5, 2, 1);
        NewsArticle first = newsArticle(1L);
        NewsArticle second = newsArticle(2L);

        when(newsRepository.findByStatus(NewsStatus.CLASSIFIED, 10)).thenReturn(List.of(first, second));
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF03_EVENT_MATCHING"), eq("NEWS"), any(), eq(AiMetricStatus.FAILED), any())).thenReturn(0L);
        doThrow(new IllegalArgumentException("event detection failed")).when(detectEventUseCase).execute(new DetectEventCommand(second.getId()));

        AutomationRunResult result = useCase.execute();

        ArgumentCaptor<DetectEventCommand> commandCaptor = ArgumentCaptor.forClass(DetectEventCommand.class);
        verify(detectEventUseCase, times(2)).execute(commandCaptor.capture());
        assertEquals(List.of(new DetectEventCommand(first.getId()), new DetectEventCommand(second.getId())), commandCaptor.getAllValues());
        assertEquals(2, result.processedCount());
        assertEquals(1, result.successCount());
        assertEquals(1, result.failedCount());
        assertEquals(second.getId(), result.errors().getFirst().entityId());
    }

    @Test
    void onlyLoadsClassifiedNewsLeavingDiscardedNewsOutOfEventDetection() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        DetectEventUseCase detectEventUseCase = mock(DetectEventUseCase.class);
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        ProcessPendingEventDetectionUseCase useCase = new ProcessPendingEventDetectionUseCase(newsRepository, detectEventUseCase, metricRepository, 10, 5, 2, 1);

        when(newsRepository.findByStatus(NewsStatus.CLASSIFIED, 10)).thenReturn(List.of());

        AutomationRunResult result = useCase.execute();

        verify(newsRepository).findByStatus(NewsStatus.CLASSIFIED, 10);
        verify(newsRepository, never()).findByStatus(NewsStatus.DISCARDED, 10);
        verify(detectEventUseCase, never()).execute(any(DetectEventCommand.class));
        assertEquals(0, result.processedCount());
    }

    @Test
    void skipsNewsWithRepeatedRecentEventMatchingFailures() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        DetectEventUseCase detectEventUseCase = mock(DetectEventUseCase.class);
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        ProcessPendingEventDetectionUseCase useCase = new ProcessPendingEventDetectionUseCase(newsRepository, detectEventUseCase, metricRepository, 10, 5, 2, 1);
        NewsArticle newsArticle = newsArticle(1L);

        when(newsRepository.findByStatus(NewsStatus.CLASSIFIED, 10)).thenReturn(List.of(newsArticle));
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF03_EVENT_MATCHING"), eq("NEWS"), eq(newsArticle.getId()), eq(AiMetricStatus.FAILED), any())).thenReturn(5L);

        AutomationRunResult result = useCase.execute();

        verify(detectEventUseCase, never()).execute(any(DetectEventCommand.class));
        assertEquals(1, result.processedCount());
        assertEquals(0, result.successCount());
        assertEquals(0, result.failedCount());
        assertEquals(1, result.skippedCount());
    }

    @Test
    void repeatedFailureQuarantineDoesNotConsumeAiBatchSlot() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        DetectEventUseCase detectEventUseCase = mock(DetectEventUseCase.class);
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        ProcessPendingEventDetectionUseCase useCase = new ProcessPendingEventDetectionUseCase(newsRepository, detectEventUseCase, metricRepository, 1, 5, 2, 3);
        NewsArticle quarantined = newsArticle(1L);
        NewsArticle processable = newsArticle(2L);

        when(newsRepository.findByStatus(NewsStatus.CLASSIFIED, 3)).thenReturn(List.of(quarantined, processable));
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF03_EVENT_MATCHING"), eq("NEWS"), eq(quarantined.getId()), eq(AiMetricStatus.FAILED), any())).thenReturn(5L);
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF03_EVENT_MATCHING"), eq("NEWS"), eq(processable.getId()), eq(AiMetricStatus.FAILED), any())).thenReturn(0L);

        AutomationRunResult result = useCase.execute();

        verify(detectEventUseCase).execute(new DetectEventCommand(processable.getId()));
        assertEquals(2, result.processedCount());
        assertEquals(1, result.successCount());
        assertEquals(0, result.failedCount());
        assertEquals(1, result.skippedCount());
    }

    private NewsArticle newsArticle(Long id) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-15T10:00:00Z");
        return new NewsArticle(
                id,
                1L,
                "Noticia " + id,
                "https://test.example/news/" + id,
                "Resumen",
                "Contenido",
                String.valueOf(id).repeat(64),
                now.minusHours(1),
                now,
                NewsStatus.CLASSIFIED,
                now,
                now
        );
    }
}
