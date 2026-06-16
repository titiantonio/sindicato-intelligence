package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.analysis.application.GenerateAnalysisCommand;
import es.sindicato.intelligence.analysis.application.GenerateAnalysisUseCase;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessPendingEventAnalysisUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPendingEventAnalysisUseCase.class);

    private final EventRepository eventRepository;
    private final EventAIAnalysisRepository analysisRepository;
    private final GenerateAnalysisUseCase generateAnalysisUseCase;
    private final int defaultLimit;

    public ProcessPendingEventAnalysisUseCase(
            EventRepository eventRepository,
            EventAIAnalysisRepository analysisRepository,
            GenerateAnalysisUseCase generateAnalysisUseCase,
            @Value("${app.automation.analysis.batch-size:10}") int defaultLimit
    ) {
        this.eventRepository = eventRepository;
        this.analysisRepository = analysisRepository;
        this.generateAnalysisUseCase = generateAnalysisUseCase;
        this.defaultLimit = defaultLimit;
    }

    public AutomationRunResult execute(RunPendingAnalysisCommand command) {
        if (command != null && command.eventId() != null) {
            return executeSingle(command.eventId());
        }

        return executePending(defaultLimit);
    }

    public AutomationRunResult executePending() {
        return executePending(defaultLimit);
    }

    public AutomationRunResult executePending(int limit) {
        int effectiveLimit = Math.max(1, limit);
        List<Event> candidates = eventRepository.findByStatusIn(List.of(EventStatus.OPEN, EventStatus.MONITORING)).stream()
                .filter(event -> !analysisRepository.existsByEventId(event.getId()))
                .limit(effectiveLimit)
                .toList();
        List<AutomationRunError> errors = new ArrayList<>();
        int successCount = 0;

        log.info("pending analysis automation started: limit={}, pendingCount={}", effectiveLimit, candidates.size());

        for (Event event : candidates) {
            try {
                generateAnalysisUseCase.execute(new GenerateAnalysisCommand(event.getId()));
                successCount++;
            } catch (RuntimeException exception) {
                errors.add(new AutomationRunError(event.getId(), truncate(exception.getMessage())));
                log.warn("pending analysis item failed: eventId={}, reason={}", event.getId(), exception.getMessage());
            }
        }

        AutomationRunResult result = new AutomationRunResult(
                candidates.size(),
                successCount,
                errors.size(),
                0,
                List.copyOf(errors)
        );

        log.info("pending analysis automation completed: processed={}, success={}, failed={}",
                result.processedCount(), result.successCount(), result.failedCount());
        return result;
    }

    private AutomationRunResult executeSingle(Long eventId) {
        log.info("manual analysis automation started: eventId={}", eventId);

        if (analysisRepository.existsByEventId(eventId)) {
            log.warn("manual analysis automation skipped because event already has analysis: eventId={}", eventId);
            return new AutomationRunResult(1, 0, 0, 1, List.of());
        }

        try {
            generateAnalysisUseCase.execute(new GenerateAnalysisCommand(eventId));
            log.info("manual analysis automation completed: eventId={}", eventId);
            return new AutomationRunResult(1, 1, 0, 0, List.of());
        } catch (RuntimeException exception) {
            log.warn("manual analysis automation failed: eventId={}, reason={}", eventId, exception.getMessage());
            return new AutomationRunResult(1, 0, 1, 0, List.of(new AutomationRunError(eventId, truncate(exception.getMessage()))));
        }
    }

    private String truncate(String message) {
        if (message == null || message.isBlank()) {
            return "automation item failed";
        }
        return message.length() <= 300 ? message : message.substring(0, 300);
    }
}
