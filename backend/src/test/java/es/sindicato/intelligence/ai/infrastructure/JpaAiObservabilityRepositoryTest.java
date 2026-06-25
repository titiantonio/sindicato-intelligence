package es.sindicato.intelligence.ai.infrastructure;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import es.sindicato.intelligence.ai.domain.AiProviderSettingRepository;
import es.sindicato.intelligence.ai.domain.AiPromptVersionRepository;
import es.sindicato.intelligence.ai.domain.AiWorkflowSettingRepository;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@Transactional
class JpaAiObservabilityRepositoryTest {

    @Autowired
    private AiOperationMetricRepository metricRepository;

    @Autowired
    private AiPromptVersionRepository promptVersionRepository;

    @Autowired
    private AiProviderSettingRepository providerSettingRepository;

    @Autowired
    private AiWorkflowSettingRepository workflowSettingRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void savesMetricAndListsRecentMetrics() {
        AiOperationMetric saved = metricRepository.save(new AiOperationMetric(
                null,
                "CLASSIFICATION",
                "WF02_CLASSIFICATION",
                "TestProvider",
                "test-model",
                AiMetricStatus.SUCCESS,
                "NEWS",
                22L,
                150,
                null,
                Map.of("category", "SIPRI", "finalNewsStatus", "CLASSIFIED"),
                OffsetDateTime.now().plusYears(100)
        ));

        List<AiOperationMetric> recent = metricRepository.findRecent(10);

        assertNotNull(saved.getId());
        assertFalse(recent.isEmpty());
        assertEquals(saved.getId(), recent.getFirst().getId());
        assertEquals("CLASSIFICATION", recent.getFirst().getOperationType());
        assertEquals("SIPRI", recent.getFirst().getOperationDetails().get("category"));
    }

    @Test
    void listsMetricsByCreatedAtRange() {
        AiOperationMetric inside = metricRepository.save(new AiOperationMetric(
                null,
                "ANALYSIS",
                "WF04_ANALYSIS",
                "TestProvider",
                "test-model",
                AiMetricStatus.FAILED,
                "EVENT",
                30L,
                250,
                "failed",
                OffsetDateTime.parse("2126-06-18T10:00:00+02:00")
        ));
        metricRepository.save(new AiOperationMetric(
                null,
                "CONTENT_GENERATION",
                "WF05_CONTENT",
                "TestProvider",
                "test-model",
                AiMetricStatus.SUCCESS,
                "EVENT",
                31L,
                150,
                null,
                OffsetDateTime.parse("2126-06-17T23:59:59+02:00")
        ));

        List<AiOperationMetric> metrics = metricRepository.findByCreatedAtBetween(
                OffsetDateTime.parse("2126-06-18T00:00:00+02:00"),
                OffsetDateTime.parse("2126-06-19T00:00:00+02:00")
        );

        assertEquals(1, metrics.size());
        assertEquals(inside.getId(), metrics.getFirst().getId());
    }

    @Test
    void listsSeededActivePromptVersions() {
        assertEquals(4, promptVersionRepository.findActive().size());
    }

    @Test
    void savesProviderApiKeyEncryptedAndReturnsDecryptedDomainValue() {
        var setting = providerSettingRepository.findByCode("gemini").orElseThrow();
        setting.update(true, "secret-api-key", true, OffsetDateTime.now());
        providerSettingRepository.save(setting);
        entityManager.flush();
        entityManager.clear();

        String storedValue = (String) entityManager.createNativeQuery("SELECT api_key_encrypted FROM ai_provider_settings WHERE provider_code = 'gemini'")
                .getSingleResult();

        assertTrue(storedValue.startsWith("enc:v1:"));
        assertEquals("secret-api-key", providerSettingRepository.findByCode("gemini").orElseThrow().getApiKey());
    }

    @Test
    void listsSeededAiWorkflowSettings() {
        assertEquals(4, workflowSettingRepository.findAll().size());
        assertNotNull(workflowSettingRepository.findByWorkflowCode("WF05_CONTENT").orElseThrow().getProviderCode());
    }
}
