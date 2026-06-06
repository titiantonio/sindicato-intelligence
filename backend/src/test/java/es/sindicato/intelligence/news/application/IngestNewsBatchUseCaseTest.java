package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class IngestNewsBatchUseCaseTest {

    @Test
    void processesBatchWithPartialResult() {
        CreateNewsUseCase createNewsUseCase = mock(CreateNewsUseCase.class);
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        NewsHashGenerator hashGenerator = new NewsHashGenerator();
        IngestNewsBatchUseCase useCase = new IngestNewsBatchUseCase(createNewsUseCase, normalizer, hashGenerator);
        CreateNewsCommand first = command("https://test.example/news/1");
        CreateNewsCommand second = command("https://test.example/news/2");

        when(createNewsUseCase.execute(first)).thenReturn(newsArticle(10L, first.url()));
        when(createNewsUseCase.execute(second)).thenThrow(new IllegalArgumentException("news url already exists"));

        IngestNewsBatchResult result = useCase.execute(new IngestNewsBatchCommand(List.of(first, second)));

        assertEquals(2, result.totalReceived());
        assertEquals(1, result.createdCount());
        assertEquals(1, result.failedCount());
        assertTrue(result.results().get(0).created());
        assertEquals(10L, result.results().get(0).newsId());
        assertFalse(result.results().get(1).created());
        assertEquals("news url already exists", result.results().get(1).error());
    }

    @Test
    void rejectsEmptyBatch() {
        CreateNewsUseCase createNewsUseCase = mock(CreateNewsUseCase.class);
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        NewsHashGenerator hashGenerator = new NewsHashGenerator();
        IngestNewsBatchUseCase useCase = new IngestNewsBatchUseCase(createNewsUseCase, normalizer, hashGenerator);

        assertThrows(IllegalArgumentException.class, () -> useCase.execute(new IngestNewsBatchCommand(List.of())));
    }

    @Test
    void rejectsNullCommand() {
        CreateNewsUseCase createNewsUseCase = mock(CreateNewsUseCase.class);
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        NewsHashGenerator hashGenerator = new NewsHashGenerator();
        IngestNewsBatchUseCase useCase = new IngestNewsBatchUseCase(createNewsUseCase, normalizer, hashGenerator);

        assertThrows(NullPointerException.class, () -> useCase.execute(null));
    }

    @Test
    void rejectsNullItems() {
        CreateNewsUseCase createNewsUseCase = mock(CreateNewsUseCase.class);
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        NewsHashGenerator hashGenerator = new NewsHashGenerator();
        IngestNewsBatchUseCase useCase = new IngestNewsBatchUseCase(createNewsUseCase, normalizer, hashGenerator);

        assertThrows(NullPointerException.class, () -> useCase.execute(new IngestNewsBatchCommand(null)));
    }

    @Test
    void normalizesRssTextBeforeCreate() {
        CreateNewsUseCase createNewsUseCase = mock(CreateNewsUseCase.class);
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        NewsHashGenerator hashGenerator = new NewsHashGenerator();
        IngestNewsBatchUseCase useCase = new IngestNewsBatchUseCase(createNewsUseCase, normalizer, hashGenerator);
        CreateNewsCommand command = new CreateNewsCommand(
                2L,
                "  Convocatoria   docente  ",
                "  https://test.example/news/normalized  ",
                "  Resumen    con   espacios ",
                "  Contenido   con   espacios ",
                OffsetDateTime.parse("2026-06-06T09:00:00Z")
        );
        when(createNewsUseCase.execute(any(CreateNewsCommand.class))).thenReturn(newsArticle(11L, "https://test.example/news/normalized"));

        IngestNewsBatchResult result = useCase.execute(new IngestNewsBatchCommand(List.of(command)));

        assertEquals(1, result.createdCount());
        assertEquals("https://test.example/news/normalized", result.results().get(0).url());
    }

    @Test
    void detectsDuplicatedUrlInsideBatch() {
        CreateNewsUseCase createNewsUseCase = mock(CreateNewsUseCase.class);
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        NewsHashGenerator hashGenerator = new NewsHashGenerator();
        IngestNewsBatchUseCase useCase = new IngestNewsBatchUseCase(createNewsUseCase, normalizer, hashGenerator);
        CreateNewsCommand first = command("https://test.example/news/duplicated-url");
        CreateNewsCommand second = command("https://test.example/news/duplicated-url");
        when(createNewsUseCase.execute(any(CreateNewsCommand.class))).thenReturn(newsArticle(12L, first.url()));

        IngestNewsBatchResult result = useCase.execute(new IngestNewsBatchCommand(List.of(first, second)));

        assertEquals(1, result.createdCount());
        assertEquals(1, result.failedCount());
        assertEquals("news url duplicated in batch", result.results().get(1).error());
    }

    @Test
    void detectsDuplicatedHashInsideBatch() {
        CreateNewsUseCase createNewsUseCase = mock(CreateNewsUseCase.class);
        NewsCaptureNormalizer normalizer = new NewsCaptureNormalizer();
        NewsHashGenerator hashGenerator = new NewsHashGenerator();
        IngestNewsBatchUseCase useCase = new IngestNewsBatchUseCase(createNewsUseCase, normalizer, hashGenerator);
        CreateNewsCommand first = new CreateNewsCommand(
                2L,
                "Misma noticia",
                "https://test.example/news/hash-1",
                "Mismo resumen",
                "Mismo contenido",
                OffsetDateTime.parse("2026-06-06T09:00:00Z")
        );
        CreateNewsCommand second = new CreateNewsCommand(
                2L,
                "Misma noticia",
                "https://test.example/news/hash-2",
                "Mismo resumen",
                "Mismo contenido",
                OffsetDateTime.parse("2026-06-06T09:00:00Z")
        );
        when(createNewsUseCase.execute(any(CreateNewsCommand.class))).thenReturn(newsArticle(13L, first.url()));

        IngestNewsBatchResult result = useCase.execute(new IngestNewsBatchCommand(List.of(first, second)));

        assertEquals(1, result.createdCount());
        assertEquals(1, result.failedCount());
        assertEquals("news hash duplicated in batch", result.results().get(1).error());
    }

    private CreateNewsCommand command(String url) {
        return new CreateNewsCommand(
                2L,
                "Convocatoria docente",
                url,
                "Resumen " + url,
                "Contenido " + url,
                OffsetDateTime.parse("2026-06-06T09:00:00Z")
        );
    }

    private NewsArticle newsArticle(Long id, String url) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new NewsArticle(
                id,
                2L,
                "Convocatoria docente",
                url,
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
