package es.sindicato.intelligence.automation.application;

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
        ProcessPendingEventDetectionUseCase useCase = new ProcessPendingEventDetectionUseCase(newsRepository, detectEventUseCase, 10);
        NewsArticle first = newsArticle(1L);
        NewsArticle second = newsArticle(2L);

        when(newsRepository.findByStatus(NewsStatus.CLASSIFIED, 10)).thenReturn(List.of(first, second));
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
        ProcessPendingEventDetectionUseCase useCase = new ProcessPendingEventDetectionUseCase(newsRepository, detectEventUseCase, 10);

        when(newsRepository.findByStatus(NewsStatus.CLASSIFIED, 10)).thenReturn(List.of());

        AutomationRunResult result = useCase.execute();

        verify(newsRepository).findByStatus(NewsStatus.CLASSIFIED, 10);
        verify(newsRepository, never()).findByStatus(NewsStatus.DISCARDED, 10);
        verify(detectEventUseCase, never()).execute(any(DetectEventCommand.class));
        assertEquals(0, result.processedCount());
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
