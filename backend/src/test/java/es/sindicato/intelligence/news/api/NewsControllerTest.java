package es.sindicato.intelligence.news.api;

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

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String hash(char character) {
        return String.valueOf(character).repeat(64);
    }
    private RequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }}
