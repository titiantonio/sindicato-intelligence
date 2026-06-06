package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class GetNewsUseCaseTest {

    @Test
    void returnsNewsWhenFound() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        GetNewsUseCase useCase = new GetNewsUseCase(newsRepository);
        NewsArticle newsArticle = newsArticle(1L);
        when(newsRepository.findById(1L)).thenReturn(Optional.of(newsArticle));

        NewsArticle result = useCase.execute(1L);

        assertEquals(newsArticle, result);
    }

    @Test
    void rejectsUnknownNews() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        GetNewsUseCase useCase = new GetNewsUseCase(newsRepository);
        when(newsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NewsNotFoundException.class, () -> useCase.execute(1L));
    }

    @Test
    void rejectsNullId() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        GetNewsUseCase useCase = new GetNewsUseCase(newsRepository);

        assertThrows(NullPointerException.class, () -> useCase.execute(null));
    }

    private NewsArticle newsArticle(Long id) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new NewsArticle(
                id,
                2L,
                "Convocatoria docente",
                "https://test.example/news/1",
                "Resumen",
                "Contenido",
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa",
                now.minusHours(1),
                now,
                NewsStatus.CAPTURED,
                now,
                now
        );
    }
}
