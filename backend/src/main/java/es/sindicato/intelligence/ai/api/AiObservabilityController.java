package es.sindicato.intelligence.ai.api;

import es.sindicato.intelligence.ai.application.AiMetricsSnapshot;
import es.sindicato.intelligence.ai.application.AiOperationMetricView;
import es.sindicato.intelligence.ai.application.ListAiMetricsUseCase;
import es.sindicato.intelligence.ai.application.ListAiPromptVersionsUseCase;
import es.sindicato.intelligence.ai.domain.AiPromptVersion;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/ai")
public class AiObservabilityController {

    private final ListAiPromptVersionsUseCase listAiPromptVersionsUseCase;
    private final ListAiMetricsUseCase listAiMetricsUseCase;

    public AiObservabilityController(
            ListAiPromptVersionsUseCase listAiPromptVersionsUseCase,
            ListAiMetricsUseCase listAiMetricsUseCase
    ) {
        this.listAiPromptVersionsUseCase = listAiPromptVersionsUseCase;
        this.listAiMetricsUseCase = listAiMetricsUseCase;
    }

    @GetMapping("/prompts")
    public List<AiPromptVersionResponse> listPrompts() {
        return listAiPromptVersionsUseCase.execute().stream()
                .map(this::toResponse)
                .toList();
    }

    @GetMapping("/metrics")
    public AiMetricsResponse listMetrics(
            @RequestParam(required = false) Integer limit,
            @RequestParam(required = false) LocalDate date
    ) {
        AiMetricsSnapshot snapshot = date == null ? listAiMetricsUseCase.execute(limit) : listAiMetricsUseCase.execute(date);
        return new AiMetricsResponse(
                snapshot.summary().totalOperations(),
                snapshot.summary().successCount(),
                snapshot.summary().failedCount(),
                snapshot.summary().averageLatencyMs(),
                snapshot.summary().p95LatencyMs(),
                snapshot.summary().successRate(),
                snapshot.summary().failureRate(),
                snapshot.summary().previousTotalOperations(),
                snapshot.summary().previousSuccessCount(),
                snapshot.summary().previousFailedCount(),
                snapshot.summary().previousAverageLatencyMs(),
                snapshot.summary().totalDifference(),
                snapshot.summary().successRateDifference(),
                snapshot.summary().failureRateDifference(),
                snapshot.summary().averageLatencyDifference(),
                snapshot.recentMetrics().stream().map(this::toResponse).toList()
        );
    }

    private AiPromptVersionResponse toResponse(AiPromptVersion prompt) {
        return new AiPromptVersionResponse(
                prompt.getPromptKey(),
                prompt.getPromptName(),
                prompt.getModule(),
                prompt.getVersion(),
                prompt.getChecksum(),
                prompt.isActive(),
                prompt.getCreatedAt()
        );
    }

    private AiMetricResponse toResponse(AiOperationMetricView metric) {
        return new AiMetricResponse(
                metric.id(),
                metric.operationType(),
                metric.promptKey(),
                metric.provider(),
                metric.model(),
                metric.status().name(),
                metric.relatedEntityType(),
                metric.relatedEntityId(),
                metric.latencyMs(),
                metric.errorMessage(),
                metric.createdAt()
        );
    }
}
