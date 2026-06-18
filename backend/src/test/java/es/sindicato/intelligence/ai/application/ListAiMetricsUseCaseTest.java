package es.sindicato.intelligence.ai.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetric;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
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

        AiMetricsSnapshot snapshot = useCase.execute((Integer) null);

        assertEquals(2, snapshot.summary().totalOperations());
        assertEquals(1, snapshot.summary().successCount());
        assertEquals(1, snapshot.summary().failedCount());
        assertEquals(200, snapshot.summary().averageLatencyMs());
    }

    @Test
    void summarizesDailyMetricsWithPreviousDayComparison() {
        AiOperationMetricRepository repository = mock(AiOperationMetricRepository.class);
        when(repository.findByCreatedAtBetween(
                OffsetDateTime.parse("2026-06-18T00:00:00+02:00"),
                OffsetDateTime.parse("2026-06-19T00:00:00+02:00")
        )).thenReturn(List.of(
                metric(AiMetricStatus.SUCCESS, 100),
                metric(AiMetricStatus.SUCCESS, 200),
                metric(AiMetricStatus.FAILED, 500)
        ));
        when(repository.findByCreatedAtBetween(
                OffsetDateTime.parse("2026-06-17T00:00:00+02:00"),
                OffsetDateTime.parse("2026-06-18T00:00:00+02:00")
        )).thenReturn(List.of(
                metric(AiMetricStatus.SUCCESS, 100),
                metric(AiMetricStatus.FAILED, 300)
        ));
        ListAiMetricsUseCase useCase = new ListAiMetricsUseCase(repository);

        AiMetricsSnapshot snapshot = useCase.execute(LocalDate.parse("2026-06-18"));

        assertEquals(3, snapshot.summary().totalOperations());
        assertEquals(2, snapshot.summary().successCount());
        assertEquals(1, snapshot.summary().failedCount());
        assertEquals(266, snapshot.summary().averageLatencyMs());
        assertEquals(500, snapshot.summary().p95LatencyMs());
        assertEquals(67, snapshot.summary().successRate());
        assertEquals(33, snapshot.summary().failureRate());
        assertEquals(2, snapshot.summary().previousTotalOperations());
        assertEquals(1, snapshot.summary().totalDifference());
        assertEquals(17, snapshot.summary().successRateDifference());
        assertEquals(-17, snapshot.summary().failureRateDifference());
        assertEquals(66, snapshot.summary().averageLatencyDifference());
        assertEquals(3, snapshot.recentMetrics().size());
    }

    @Test
    void keepsLimitModeWhenDateIsNotUsed() {
        AiOperationMetricRepository repository = mock(AiOperationMetricRepository.class);
        when(repository.findRecent(10)).thenReturn(List.of(metric(AiMetricStatus.SUCCESS, 100)));
        ListAiMetricsUseCase useCase = new ListAiMetricsUseCase(repository);

        useCase.execute(10);

        verify(repository).findRecent(10);
        verify(repository, org.mockito.Mockito.never()).findByCreatedAtBetween(any(), any());
    }

    private AiOperationMetric metric(AiMetricStatus status, long latencyMs) {
        return new AiOperationMetric(
                null,
                "CLASSIFICATION",
                "WF02_CLASSIFICATION",
                "TestProvider",
                "test-model",
                status,
                "NEWS",
                1L,
                latencyMs,
                status == AiMetricStatus.FAILED ? "failed" : null,
                OffsetDateTime.parse("2026-06-18T10:00:00Z")
        );
    }
}
