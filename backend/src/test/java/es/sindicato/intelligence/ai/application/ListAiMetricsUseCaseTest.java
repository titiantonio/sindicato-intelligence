package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class ListAiMetricsUseCaseTest {

    @Test
    void summarizesRecentMetrics() {
        AiOperationMetricRepository repository = mock(AiOperationMetricRepository.class);
        when(repository.findRecent(50)).thenReturn(List.of(
                metric(AiMetricStatus.SUCCESS, 100),
                metric(AiMetricStatus.FAILED, 300)
        ));
        ListAiMetricsUseCase useCase = new ListAiMetricsUseCase(repository);

        AiMetricsSnapshot snapshot = useCase.execute(null);

        assertEquals(2, snapshot.summary().totalOperations());
        assertEquals(1, snapshot.summary().successCount());
        assertEquals(1, snapshot.summary().failedCount());
        assertEquals(200, snapshot.summary().averageLatencyMs());
    }

    private AiOperationMetric metric(AiMetricStatus status, long latencyMs) {
        return new AiOperationMetric(
                null,
                "CLASSIFICATION",
                "WF02_CLASSIFICATION",
                "TestProvider",
                null,
                status,
                "NEWS",
                1L,
                latencyMs,
                status == AiMetricStatus.FAILED ? "failed" : null,
                OffsetDateTime.parse("2026-06-18T10:00:00Z")
        );
    }
}
