package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateNewsUseCaseTest {

    @Test
    void createsNewsWhenSourceExistsAndNewsIsNotDuplicated() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        SourceRepository sourceRepository = mock(SourceRepository.class);
        CreateNewsUseCase useCase = new CreateNewsUseCase(newsRepository, sourceRepository);
        CreateNewsCommand command = command();
        NewsArticle savedNewsArticle = newsArticle(1L, "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        when(sourceRepository.findById(command.sourceId())).thenReturn(Optional.of(source(command.sourceId())));
        when(newsRepository.findByUrl(command.url())).thenReturn(Optional.empty());
        when(newsRepository.findByHash(anyString())).thenReturn(Optional.empty());
        when(newsRepository.save(any(NewsArticle.class))).thenReturn(savedNewsArticle);

        NewsArticle result = useCase.execute(command);

        ArgumentCaptor<NewsArticle> newsArticleCaptor = ArgumentCaptor.forClass(NewsArticle.class);
        verify(newsRepository).save(newsArticleCaptor.capture());
        NewsArticle newsArticleToSave = newsArticleCaptor.getValue();

        assertEquals(savedNewsArticle, result);
        assertEquals(command.sourceId(), newsArticleToSave.getSourceId());
        assertEquals(command.title(), newsArticleToSave.getTitle());
        assertEquals(command.url(), newsArticleToSave.getUrl());
        assertEquals(command.summary(), newsArticleToSave.getSummary());
        assertEquals(command.content(), newsArticleToSave.getContent());
        assertEquals(64, newsArticleToSave.getHash().length());
        assertEquals(NewsStatus.CAPTURED, newsArticleToSave.getProcessingStatus());
        assertNotNull(newsArticleToSave.getCapturedAt());
        assertNotNull(newsArticleToSave.getCreatedAt());
        assertNotNull(newsArticleToSave.getUpdatedAt());
    }

    @Test
    void rejectsUnknownSource() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        SourceRepository sourceRepository = mock(SourceRepository.class);
        CreateNewsUseCase useCase = new CreateNewsUseCase(newsRepository, sourceRepository);
        CreateNewsCommand command = command();

        when(sourceRepository.findById(command.sourceId())).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

        verify(newsRepository, never()).save(any(NewsArticle.class));
    }

    @Test
    void rejectsDuplicatedUrl() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        SourceRepository sourceRepository = mock(SourceRepository.class);
        CreateNewsUseCase useCase = new CreateNewsUseCase(newsRepository, sourceRepository);
        CreateNewsCommand command = command();

        when(sourceRepository.findById(command.sourceId())).thenReturn(Optional.of(source(command.sourceId())));
        when(newsRepository.findByUrl(command.url())).thenReturn(Optional.of(newsArticle(1L, hash('a'))));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

        verify(newsRepository, never()).save(any(NewsArticle.class));
    }

    @Test
    void rejectsDuplicatedHash() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        SourceRepository sourceRepository = mock(SourceRepository.class);
        CreateNewsUseCase useCase = new CreateNewsUseCase(newsRepository, sourceRepository);
        CreateNewsCommand command = command();

        when(sourceRepository.findById(command.sourceId())).thenReturn(Optional.of(source(command.sourceId())));
        when(newsRepository.findByUrl(command.url())).thenReturn(Optional.empty());
        when(newsRepository.findByHash(anyString())).thenReturn(Optional.of(newsArticle(1L, hash('b'))));

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(command));

        verify(newsRepository, never()).save(any(NewsArticle.class));
    }

    @Test
    void rejectsNullCommand() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        SourceRepository sourceRepository = mock(SourceRepository.class);
        CreateNewsUseCase useCase = new CreateNewsUseCase(newsRepository, sourceRepository);

        assertThrows(NullPointerException.class, () -> useCase.execute(null));
    }

    private CreateNewsCommand command() {
        return new CreateNewsCommand(
                2L,
                "Convocatoria docente",
                "https://test.example/news/1",
                "Resumen",
                "Contenido",
                OffsetDateTime.parse("2026-06-06T09:00:00Z")
        );
    }

    private Source source(Long id) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new Source(
                id,
                "Fuente Test",
                "https://test.example/source",
                "RSS",
                50,
                true,
                now,
                now
        );
    }

    private NewsArticle newsArticle(Long id, String hash) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new NewsArticle(
                id,
                2L,
                "Convocatoria docente",
                "https://test.example/news/1",
                "Resumen",
                "Contenido",
                hash,
                now.minusHours(1),
                now,
                NewsStatus.CAPTURED,
                now,
                now
        );
    }

    private String hash(char character) {
        return String.valueOf(character).repeat(64);
    }
}
