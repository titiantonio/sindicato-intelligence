package es.sindicato.intelligence.news.infrastructure;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JpaNewsRepositoryTest {

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Test
    void savesAndFindsNewsArticleById() {
        Source source = saveSource("https://test.example/source-news-by-id");
        NewsArticle newsArticle = newsArticle(source.getId(), "https://test.example/news-by-id", hash('a'));

        NewsArticle saved = newsRepository.save(newsArticle);
        Optional<NewsArticle> found = newsRepository.findById(saved.getId());

        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(saved.getId(), found.get().getId());
        assertEquals(source.getId(), found.get().getSourceId());
        assertEquals("Convocatoria docente", found.get().getTitle());
        assertEquals(NewsStatus.CAPTURED, found.get().getProcessingStatus());
    }

    @Test
    void findsNewsArticleByUrl() {
        Source source = saveSource("https://test.example/source-news-by-url");
        newsRepository.save(newsArticle(source.getId(), "https://test.example/news-by-url", hash('b')));

        Optional<NewsArticle> found = newsRepository.findByUrl("https://test.example/news-by-url");

        assertTrue(found.isPresent());
        assertEquals("https://test.example/news-by-url", found.get().getUrl());
    }

    @Test
    void findsNewsArticleByHash() {
        Source source = saveSource("https://test.example/source-news-by-hash");
        String hash = hash('c');
        newsRepository.save(newsArticle(source.getId(), "https://test.example/news-by-hash", hash));

        Optional<NewsArticle> found = newsRepository.findByHash(hash);

        assertTrue(found.isPresent());
        assertEquals(hash, found.get().getHash());
    }

    @Test
    void listsSavedNewsArticles() {
        Source source = saveSource("https://test.example/source-news-list");
        NewsArticle savedFirst = newsRepository.save(newsArticle(source.getId(), "https://test.example/news-list-1", hash('d')));
        NewsArticle savedSecond = newsRepository.save(newsArticle(source.getId(), "https://test.example/news-list-2", hash('e')));

        List<NewsArticle> newsArticles = newsRepository.findAll();

        assertTrue(newsArticles.stream().anyMatch(newsArticle -> newsArticle.getId().equals(savedFirst.getId())));
        assertTrue(newsArticles.stream().anyMatch(newsArticle -> newsArticle.getId().equals(savedSecond.getId())));
    }

    private Source saveSource(String url) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        return sourceRepository.save(new Source(
                null,
                "Fuente Test News",
                url,
                "RSS",
                50,
                true,
                now,
                now
        ));
    }

    private NewsArticle newsArticle(Long sourceId, String url, String hash) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");

        return new NewsArticle(
                null,
                sourceId,
                "Convocatoria docente",
                url,
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
