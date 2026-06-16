package es.sindicato.intelligence.automation.application;

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
        ProcessPendingClassificationsUseCase useCase = new ProcessPendingClassificationsUseCase(newsRepository, classifyNewsUseCase, 10);
        NewsArticle first = newsArticle(1L);
        NewsArticle second = newsArticle(2L);

        when(newsRepository.findByStatus(NewsStatus.CAPTURED, 10)).thenReturn(List.of(first, second));
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
                NewsStatus.CAPTURED,
                now,
                now
        );
    }
}
