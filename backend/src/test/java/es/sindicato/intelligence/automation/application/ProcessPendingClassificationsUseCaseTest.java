package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import es.sindicato.intelligence.classification.application.ClassifyNewsCommand;
import es.sindicato.intelligence.classification.application.ClassifyNewsUseCase;
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
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class ProcessPendingClassificationsUseCaseTest {

    @Test
    void processesCapturedNewsAndContinuesAfterItemFailure() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        ClassifyNewsUseCase classifyNewsUseCase = mock(ClassifyNewsUseCase.class);
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        ProcessPendingClassificationsUseCase useCase = new ProcessPendingClassificationsUseCase(newsRepository, classifyNewsUseCase, metricRepository, 10, 5, 24, 1);
        NewsArticle first = newsArticle(1L);
        NewsArticle second = newsArticle(2L);

        when(newsRepository.findByStatus(NewsStatus.CAPTURED, 10)).thenReturn(List.of(first, second));
        when(classifyNewsUseCase.prioritySignals(first)).thenReturn(List.of());
        when(classifyNewsUseCase.prioritySignals(second)).thenReturn(List.of());
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF02_CLASSIFICATION"), eq("NEWS"), any(), eq(AiMetricStatus.FAILED), any())).thenReturn(0L);
        doThrow(new IllegalArgumentException("classification failed")).when(classifyNewsUseCase).execute(new ClassifyNewsCommand(second.getId()));

        AutomationRunResult result = useCase.execute();

        ArgumentCaptor<ClassifyNewsCommand> commandCaptor = ArgumentCaptor.forClass(ClassifyNewsCommand.class);
        verify(classifyNewsUseCase, times(2)).execute(commandCaptor.capture());
        assertEquals(2, result.processedCount());
        assertEquals(List.of(new ClassifyNewsCommand(first.getId()), new ClassifyNewsCommand(second.getId())), commandCaptor.getAllValues());
        assertEquals(1, result.successCount());
        assertEquals(1, result.failedCount());
        assertEquals(0, result.skippedCount());
        assertEquals(second.getId(), result.errors().getFirst().entityId());
    }

    @Test
    void prioritizesCapturedNewsWithOperationalSignals() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        ClassifyNewsUseCase classifyNewsUseCase = mock(ClassifyNewsUseCase.class);
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        ProcessPendingClassificationsUseCase useCase = new ProcessPendingClassificationsUseCase(newsRepository, classifyNewsUseCase, metricRepository, 1, 5, 24, 3);
        NewsArticle oldLowPriority = newsArticle(1L, "Noticia de contexto educativo");
        NewsArticle urgent = newsArticle(2L, "BOJA publica convocatoria SIPRI con plazo abierto");
        NewsArticle anotherLowPriority = newsArticle(3L, "Opinion sobre educacion");

        when(newsRepository.findByStatus(NewsStatus.CAPTURED, 3)).thenReturn(List.of(oldLowPriority, urgent, anotherLowPriority));
        when(classifyNewsUseCase.prioritySignals(oldLowPriority)).thenReturn(List.of());
        when(classifyNewsUseCase.prioritySignals(urgent)).thenReturn(List.of("BOJA", "SIPRI", "PLAZO"));
        when(classifyNewsUseCase.prioritySignals(anotherLowPriority)).thenReturn(List.of());
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF02_CLASSIFICATION"), eq("NEWS"), any(), eq(AiMetricStatus.FAILED), any())).thenReturn(0L);

        AutomationRunResult result = useCase.execute();

        ArgumentCaptor<ClassifyNewsCommand> commandCaptor = ArgumentCaptor.forClass(ClassifyNewsCommand.class);
        verify(classifyNewsUseCase).execute(commandCaptor.capture());
        assertEquals(urgent.getId(), commandCaptor.getValue().newsId());
        assertEquals(1, result.processedCount());
        assertEquals(1, result.successCount());
    }

    @Test
    void skipsNewsWithRepeatedRecentClassificationFailures() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        ClassifyNewsUseCase classifyNewsUseCase = mock(ClassifyNewsUseCase.class);
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        ProcessPendingClassificationsUseCase useCase = new ProcessPendingClassificationsUseCase(newsRepository, classifyNewsUseCase, metricRepository, 10, 5, 24, 1);
        NewsArticle newsArticle = newsArticle(1L);

        when(newsRepository.findByStatus(NewsStatus.CAPTURED, 10)).thenReturn(List.of(newsArticle));
        when(classifyNewsUseCase.prioritySignals(newsArticle)).thenReturn(List.of());
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF02_CLASSIFICATION"), eq("NEWS"), eq(newsArticle.getId()), eq(AiMetricStatus.FAILED), any())).thenReturn(5L);

        AutomationRunResult result = useCase.execute();

        verify(classifyNewsUseCase, times(0)).execute(any(ClassifyNewsCommand.class));
        assertEquals(1, result.processedCount());
        assertEquals(0, result.successCount());
        assertEquals(0, result.failedCount());
        assertEquals(1, result.skippedCount());
    }

    @Test
    void repeatedFailureQuarantineDoesNotConsumeAiBatchSlot() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        ClassifyNewsUseCase classifyNewsUseCase = mock(ClassifyNewsUseCase.class);
        AiOperationMetricRepository metricRepository = mock(AiOperationMetricRepository.class);
        ProcessPendingClassificationsUseCase useCase = new ProcessPendingClassificationsUseCase(newsRepository, classifyNewsUseCase, metricRepository, 1, 5, 24, 3);
        NewsArticle quarantined = newsArticle(1L, "BOJA publica convocatoria SIPRI con plazo abierto");
        NewsArticle processable = newsArticle(2L, "Noticia educativa procesable");

        when(newsRepository.findByStatus(NewsStatus.CAPTURED, 3)).thenReturn(List.of(quarantined, processable));
        when(classifyNewsUseCase.prioritySignals(quarantined)).thenReturn(List.of("BOJA", "SIPRI", "PLAZO"));
        when(classifyNewsUseCase.prioritySignals(processable)).thenReturn(List.of());
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF02_CLASSIFICATION"), eq("NEWS"), eq(quarantined.getId()), eq(AiMetricStatus.FAILED), any())).thenReturn(5L);
        when(metricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(eq("WF02_CLASSIFICATION"), eq("NEWS"), eq(processable.getId()), eq(AiMetricStatus.FAILED), any())).thenReturn(0L);

        AutomationRunResult result = useCase.execute();

        verify(classifyNewsUseCase).execute(new ClassifyNewsCommand(processable.getId()));
        assertEquals(2, result.processedCount());
        assertEquals(1, result.successCount());
        assertEquals(1, result.skippedCount());
    }

    private NewsArticle newsArticle(Long id) {
        return newsArticle(id, "Noticia " + id);
    }

    private NewsArticle newsArticle(Long id, String title) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-15T10:00:00Z");
        return new NewsArticle(
                id,
                1L,
                title,
                "https://test.example/news/" + id,
                "Resumen",
                "Contenido",
                String.valueOf(id).repeat(64),
                now.minusHours(1),
                now,
                NewsStatus.CAPTURED,
                now,
                now
        );
    }
}
