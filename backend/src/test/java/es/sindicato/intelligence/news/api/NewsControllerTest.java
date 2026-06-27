package es.sindicato.intelligence.news.api;

import es.sindicato.intelligence.classification.domain.ClassificationCategory;
import es.sindicato.intelligence.classification.domain.ImpactLevel;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.classification.domain.UrgencyLevel;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class NewsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private NewsClassificationRepository classificationRepository;

    @Test
    void createsNews() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        String newsUrl = uniqueUrl("news");

        mockMvc.perform(post("/api/v1/news").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceId": %d,
                                  "title": "Convocatoria docente",
                                  "url": "%s",
                                  "summary": "Resumen",
                                  "content": "Contenido",
                                  "publishedAt": "2026-06-06T09:00:00Z"
                                }
                                """.formatted(source.getId(), newsUrl)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.sourceId").value(source.getId()))
                .andExpect(jsonPath("$.title").value("Convocatoria docente"))
                .andExpect(jsonPath("$.url").value(newsUrl))
                .andExpect(jsonPath("$.summary").value("Resumen"))
                .andExpect(jsonPath("$.content").value("Contenido"))
                .andExpect(jsonPath("$.hash", notNullValue()))
                .andExpect(jsonPath("$.processingStatus").value("CAPTURED"))
                .andExpect(jsonPath("$.capturedAt", notNullValue()))
                .andExpect(jsonPath("$.createdAt", notNullValue()))
                .andExpect(jsonPath("$.updatedAt", notNullValue()));
    }

    @Test
    void listsNews() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news"), hash('a')));

        mockMvc.perform(get("/api/v1/news").with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[*].id", hasItem(newsArticle.getId().intValue())))
                .andExpect(jsonPath("$[*].title", hasItem("Convocatoria docente")));
    }

    @Test
    void listsNewsPageWithNewestNewsFirstByDefault() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        String marker = "PAGE_DEFAULT_" + UUID.randomUUID();
        NewsArticle oldNews = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-old"), hash('d'), OffsetDateTime.parse("2026-06-10T10:00:00Z"), NewsStatus.CAPTURED, marker + " antigua"));
        NewsArticle newNews = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-new"), hash('e'), OffsetDateTime.parse("2026-06-12T10:00:00Z"), NewsStatus.CAPTURED, marker + " nueva"));

        mockMvc.perform(get("/api/v1/news/page")
                        .param("global", marker)
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.pageSize").value(10))
                .andExpect(jsonPath("$.totalItems").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.items[0].id").value(newNews.getId()))
                .andExpect(jsonPath("$.items[1].id").value(oldNews.getId()));
    }

    @Test
    void listsNewsPageWithRequestedPageAndSize() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        String marker = "PAGE_SIZE_" + UUID.randomUUID();
        NewsArticle oldest = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-1"), hash('f'), OffsetDateTime.parse("2026-06-10T10:00:00Z"), NewsStatus.CAPTURED, marker + " 1"));
        newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-2"), hash('g'), OffsetDateTime.parse("2026-06-11T10:00:00Z"), NewsStatus.CAPTURED, marker + " 2"));
        newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-3"), hash('h'), OffsetDateTime.parse("2026-06-12T10:00:00Z"), NewsStatus.CAPTURED, marker + " 3"));

        mockMvc.perform(get("/api/v1/news/page")
                        .param("page", "2")
                        .param("pageSize", "2")
                        .param("global", marker)
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(2))
                .andExpect(jsonPath("$.pageSize").value(2))
                .andExpect(jsonPath("$.totalItems").value(3))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.items", hasSize(1)))
                .andExpect(jsonPath("$.items[0].id").value(oldest.getId()));
    }

    @Test
    void filtersNewsPageByStatusSourceEventCategoryAndGlobalSearch() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        NewsArticle matching = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-match"), hash('i'), OffsetDateTime.parse("2026-06-12T10:00:00Z"), NewsStatus.EVENT_MATCHED, "Oposiciones Andalucia"));
        NewsArticle other = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-other"), hash('j'), OffsetDateTime.parse("2026-06-11T10:00:00Z"), NewsStatus.CAPTURED, "Formacion"));
        Event event = eventRepository.save(event(matching.getId()));
        classificationRepository.save(classification(matching.getId(), ClassificationCategory.OPOSICIONES));
        classificationRepository.save(classification(other.getId(), ClassificationCategory.FORMACION));

        mockMvc.perform(get("/api/v1/news/page")
                        .param("status", "EVENT_MATCHED")
                        .param("source", "Fuente #" + source.getId())
                        .param("event", "#" + event.getId())
                        .param("category", "OPOSICIONES")
                        .param("global", "Andalucia")
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalItems").value(1))
                .andExpect(jsonPath("$.items[0].id").value(matching.getId()))
                .andExpect(jsonPath("$.items[0].eventId").value(event.getId()))
                .andExpect(jsonPath("$.items[0].category").value("OPOSICIONES"));
    }

    @Test
    void fallsBackToDefaultSortForInvalidSortValues() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        String marker = "INVALID_SORT_" + UUID.randomUUID();
        NewsArticle oldNews = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-invalid-sort-old"), hash('k'), OffsetDateTime.parse("2026-06-10T10:00:00Z"), NewsStatus.CAPTURED, marker + " antigua"));
        NewsArticle newNews = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news-invalid-sort-new"), hash('l'), OffsetDateTime.parse("2026-06-12T10:00:00Z"), NewsStatus.CAPTURED, marker + " nueva"));

        mockMvc.perform(get("/api/v1/news/page")
                        .param("sortColumn", "unsupported")
                        .param("sortDirection", "unsupported")
                        .param("global", marker)
                        .with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.items[0].id").value(newNews.getId()))
                .andExpect(jsonPath("$.items[1].id").value(oldNews.getId()));
    }

    @Test
    void getsNewsById() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId(), uniqueUrl("news"), hash('b')));

        mockMvc.perform(get("/api/v1/news/{id}", newsArticle.getId()).with(adminJwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(newsArticle.getId()))
                .andExpect(jsonPath("$.sourceId").value(source.getId()))
                .andExpect(jsonPath("$.title").value("Convocatoria docente"))
                .andExpect(jsonPath("$.processingStatus").value("CAPTURED"));
    }

    @Test
    void rejectsInvalidCreateRequest() throws Exception {
        mockMvc.perform(post("/api/v1/news").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "sourceId": 0,
                                  "title": "",
                                  "url": "not-a-url",
                                  "summary": "Resumen",
                                  "content": "Contenido"
                                }
                                """))
                .andExpect(status().isBadRequest());
    }

    @Test
    void returnsNotFoundWhenGettingMissingNews() throws Exception {
        mockMvc.perform(get("/api/v1/news/{id}", 999999L).with(adminJwt()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    @Test
    void ingestsNewsBatchWithPartialProcessing() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        String duplicatedUrl = uniqueUrl("news-duplicated-url");

        mockMvc.perform(post("/api/v1/news/bulk").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {
                                    "sourceId": %d,
                                    "title": "Convocatoria docente",
                                    "url": "%s",
                                    "summary": "Resumen 1",
                                    "content": "Contenido 1",
                                    "publishedAt": "2026-06-06T09:00:00Z"
                                  },
                                  {
                                    "sourceId": %d,
                                    "title": "Convocatoria docente",
                                    "url": "%s",
                                    "summary": "Resumen 2",
                                    "content": "Contenido 2",
                                    "publishedAt": "2026-06-06T10:00:00Z"
                                  }
                                ]
                                """.formatted(source.getId(), duplicatedUrl, source.getId(), duplicatedUrl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceived").value(2))
                .andExpect(jsonPath("$.createdCount").value(1))
                .andExpect(jsonPath("$.failedCount").value(1))
                .andExpect(jsonPath("$.results", hasSize(2)))
                .andExpect(jsonPath("$.results[0].created").value(true))
                .andExpect(jsonPath("$.results[0].newsId", notNullValue()))
                .andExpect(jsonPath("$.results[1].created").value(false))
                .andExpect(jsonPath("$.results[1].error").value("news url duplicated in batch"));
    }

    @Test
    void ingestsNewsBatchDetectingDuplicateAgainstDatabase() throws Exception {
        Source source = sourceRepository.save(source(uniqueUrl("sources")));
        String existingUrl = uniqueUrl("news-existing");
        newsRepository.save(newsArticle(source.getId(), existingUrl, hash('c')));
        String newUrl = uniqueUrl("news-new");

        mockMvc.perform(post("/api/v1/news/bulk").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                [
                                  {
                                    "sourceId": %d,
                                    "title": "Noticia existente",
                                    "url": "%s",
                                    "summary": "Resumen existente",
                                    "content": "Contenido existente",
                                    "publishedAt": "2026-06-06T09:00:00Z"
                                  },
                                  {
                                    "sourceId": %d,
                                    "title": "Noticia nueva",
                                    "url": "%s",
                                    "summary": "Resumen nuevo",
                                    "content": "Contenido nuevo",
                                    "publishedAt": "2026-06-06T11:00:00Z"
                                  }
                                ]
                                """.formatted(source.getId(), existingUrl, source.getId(), newUrl)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalReceived").value(2))
                .andExpect(jsonPath("$.createdCount").value(1))
                .andExpect(jsonPath("$.failedCount").value(1))
                .andExpect(jsonPath("$.results[0].created").value(false))
                .andExpect(jsonPath("$.results[0].error").value("news url already exists"))
                .andExpect(jsonPath("$.results[1].created").value(true));
    }

    @Test
    void rejectsEmptyBatchRequest() throws Exception {
        mockMvc.perform(post("/api/v1/news/bulk").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("[]"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("news batch cannot be empty"));
    }

    private Source source(String url) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);

        return new Source(
                null,
                "Fuente News API",
                url,
                "RSS",
                10,
                true,
                now,
                now
        );
    }

    private NewsArticle newsArticle(Long sourceId, String url, String hash) {
        OffsetDateTime now = OffsetDateTime.now().minusDays(1);
        return newsArticle(sourceId, url, hash, now);
    }

    private NewsArticle newsArticle(Long sourceId, String url, String hash, OffsetDateTime capturedAt) {
        return newsArticle(sourceId, url, hash, capturedAt, NewsStatus.CAPTURED, "Convocatoria docente");
    }

    private NewsArticle newsArticle(Long sourceId, String url, String hash, OffsetDateTime capturedAt, NewsStatus status, String title) {
        return new NewsArticle(
                null,
                sourceId,
                title,
                url,
                "Resumen",
                "Contenido",
                hash,
                capturedAt.minusHours(1),
                capturedAt,
                status,
                capturedAt,
                capturedAt
        );
    }

    private Event event(Long newsId) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-12T12:00:00Z");
        return new Event(
                null,
                "Evento oposiciones",
                "Descripcion",
                EventCategory.OPOSICIONES,
                Importance.HIGH,
                EventStatus.OPEN,
                Set.of(newsId),
                now,
                now,
                now,
                now
        );
    }

    private NewsClassification classification(Long newsId, ClassificationCategory category) {
        return new NewsClassification(
                null,
                newsId,
                category,
                null,
                BigDecimal.valueOf(80),
                ImpactLevel.HIGH,
                UrgencyLevel.MEDIUM,
                java.util.List.of("docentes"),
                java.util.List.of("Andalucia"),
                OffsetDateTime.parse("2026-06-12T12:30:00Z")
        );
    }

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String hash(char character) {
        return String.valueOf(character).repeat(64);
    }
    private RequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }}
