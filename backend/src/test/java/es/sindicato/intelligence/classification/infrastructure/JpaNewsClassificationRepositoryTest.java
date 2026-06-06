package es.sindicato.intelligence.classification.infrastructure;

import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JpaNewsClassificationRepositoryTest {

    @Autowired
    private NewsClassificationRepository classificationRepository;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Test
    void savesAndFindsClassificationByNewsId() {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId()));
        NewsClassification saved = classificationRepository.save(classification(newsArticle.getId()));

        Optional<NewsClassification> found = classificationRepository.findByNewsId(newsArticle.getId());

        assertNotNull(saved.getId());
        assertTrue(found.isPresent());
        assertEquals(ClassificationCategory.SIPRI, found.get().getCategory());
        assertEquals(List.of("SIPRI"), found.get().getKeywords());
        assertTrue(classificationRepository.existsByNewsId(newsArticle.getId()));
    }

    private Source source() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        return new Source(null, "Fuente Classification", uniqueUrl("sources"), "RSS", 10, true, now, now);
    }

    private NewsArticle newsArticle(Long sourceId) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        return new NewsArticle(
                null,
                sourceId,
                "SIPRI publica adjudicaciones",
                uniqueUrl("news"),
                "Resumen",
                "Contenido",
                uniqueHash(),
                now.minusHours(1),
                now,
                NewsStatus.CAPTURED,
                now,
                now
        );
    }

    private NewsClassification classification(Long newsId) {
        return new NewsClassification(
                null,
                newsId,
                ClassificationCategory.SIPRI,
                "Adjudicaciones",
                BigDecimal.valueOf(95),
                ImpactLevel.HIGH,
                UrgencyLevel.HIGH,
                List.of("SIPRI"),
                List.of("Junta de Andalucia"),
                OffsetDateTime.parse("2026-06-06T10:00:00Z")
        );
    }

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String uniqueHash() {
        String value = UUID.randomUUID().toString().replace("-", "");
        return value + value;
    }
}
