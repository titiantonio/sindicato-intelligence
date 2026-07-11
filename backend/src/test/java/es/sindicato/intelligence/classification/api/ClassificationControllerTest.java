package es.sindicato.intelligence.classification.api;

import es.sindicato.intelligence.ai.domain.AiProviderSetting;
import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.ai.domain.AiWorkflowSetting;
import es.sindicato.intelligence.ai.domain.AiWorkflowSettingRepository;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClassificationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private SourceRepository sourceRepository;

    @Autowired
    private NewsRepository newsRepository;

    @Autowired
    private NewsClassificationRepository classificationRepository;

    @Autowired
    private AiProviderSettingRepository providerSettingRepository;

    @Autowired
    private AiWorkflowSettingRepository workflowSettingRepository;

    @BeforeEach
    void configureDeterministicAi() {
        OffsetDateTime now = OffsetDateTime.now();
        AiProviderSetting provider = providerSettingRepository.findByCode("deterministic")
                .orElse(new AiProviderSetting("deterministic", "Deterministic", true, null, now, now));
        provider.update(true, null, false, now);
        providerSettingRepository.save(provider);

        AiWorkflowSetting workflow = workflowSettingRepository.findByWorkflowCode("WF02_CLASSIFICATION")
                .orElse(new AiWorkflowSetting("WF02_CLASSIFICATION", "deterministic", "deterministic-classification", BigDecimal.ZERO, 1024, 60, now, now));
        workflow.update("deterministic", "deterministic-classification", BigDecimal.ZERO, 1024, 60, now);
        workflowSettingRepository.save(workflow);
    }

    @Test
    void classifiesNewsAndPersistsClassification() throws Exception {
        Source source = sourceRepository.save(source());
        NewsArticle newsArticle = newsRepository.save(newsArticle(source.getId(), "SIPRI publica adjudicaciones"));

        mockMvc.perform(post("/api/v1/classifications/classify").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "newsId": %d
                                }
                                """.formatted(newsArticle.getId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.newsId").value(newsArticle.getId()))
                .andExpect(jsonPath("$.category").value("SIPRI"))
                .andExpect(jsonPath("$.relevanceScore").value(95))
                .andExpect(jsonPath("$.impactLevel").value("HIGH"))
                .andExpect(jsonPath("$.urgencyLevel").value("HIGH"));

        assertTrue(classificationRepository.existsByNewsId(newsArticle.getId()));
        assertEquals(NewsStatus.CLASSIFIED, newsRepository.findById(newsArticle.getId()).orElseThrow().getProcessingStatus());
    }

    @Test
    void rejectsMissingNews() throws Exception {
        mockMvc.perform(post("/api/v1/classifications/classify").with(adminJwt())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "newsId": 999999
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", notNullValue()));
    }

    private Source source() {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        return new Source(null, "Fuente Classification API", uniqueUrl("sources"), "RSS", 10, true, now, now);
    }

    private NewsArticle newsArticle(Long sourceId, String title) {
        OffsetDateTime now = OffsetDateTime.parse("2026-06-06T10:00:00Z");
        return new NewsArticle(
                null,
                sourceId,
                title,
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

    private String uniqueUrl(String type) {
        return "https://test.example/" + type + "/" + UUID.randomUUID();
    }

    private String uniqueHash() {
        String value = UUID.randomUUID().toString().replace("-", "");
        return value + value;
    }
    private RequestPostProcessor adminJwt() {
        return jwt().authorities(() -> "ROLE_ADMIN");
    }
}
