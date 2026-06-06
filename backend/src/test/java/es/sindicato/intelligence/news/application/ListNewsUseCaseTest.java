package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListNewsUseCaseTest {

    @Test
    void returnsAllNews() {
        NewsRepository newsRepository = mock(NewsRepository.class);
        ListNewsUseCase useCase = new ListNewsUseCase(newsRepository);
        List<NewsArticle> newsArticles = List.of(newsArticle(1L), newsArticle(2L));
        when(newsRepository.findAll()).thenReturn(newsArticles);

        List<NewsArticle> result = useCase.execute();

        assertEquals(newsArticles, result);
    }

    private NewsArticle newsArticle(Long id) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new NewsArticle(
                id,
                2L,
                "Convocatoria docente " + id,
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
