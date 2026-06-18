package es.sindicato.intelligence.ai.infrastructure;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import es.sindicato.intelligence.ai.domain.AiPromptVersionRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@Transactional
class JpaAiObservabilityRepositoryTest {

    @Autowired
    private AiOperationMetricRepository metricRepository;

    @Autowired
    private AiPromptVersionRepository promptVersionRepository;

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
                OffsetDateTime.now().plusYears(100)
        ));

        List<AiOperationMetric> recent = metricRepository.findRecent(10);

        assertNotNull(saved.getId());
        assertFalse(recent.isEmpty());
        assertEquals(saved.getId(), recent.getFirst().getId());
        assertEquals("CLASSIFICATION", recent.getFirst().getOperationType());
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
}
