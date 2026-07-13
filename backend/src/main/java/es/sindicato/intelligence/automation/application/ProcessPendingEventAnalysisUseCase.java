package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.analysis.domain.AnalysisGenerationTrigger;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.application.GenerateAnalysisCommand;
import es.sindicato.intelligence.analysis.application.GenerateAnalysisUseCase;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class ProcessPendingEventAnalysisUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPendingEventAnalysisUseCase.class);
    private static final int LOW_IMPORTANCE_MIN_NEWS = 3;
    private static final long STANDARD_STABILIZATION_MINUTES = 15;

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
        List<EventAnalysisCandidate> candidates = eventRepository.findByStatusIn(List.of(EventStatus.OPEN, EventStatus.MONITORING)).stream()
                .filter(event -> !event.isManualDiscarded())
                .map(event -> new EventAnalysisCandidate(event, analysisRepository.findLatestByEventId(event.getId())))
                .filter(this::isEligibleForAutomaticAnalysis)
                .sorted(candidateComparator())
                .limit(effectiveLimit)
                .toList();
        List<AutomationRunError> errors = new ArrayList<>();
        int successCount = 0;

        log.info("pending analysis automation started: limit={}, pendingCount={}", effectiveLimit, candidates.size());

        for (EventAnalysisCandidate candidate : candidates) {
            Event event = candidate.event();
            try {
                generateAnalysisUseCase.execute(new GenerateAnalysisCommand(event.getId(), triggerFor(candidate)));
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

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("event not found: " + eventId));
        Optional<EventAIAnalysis> latestAnalysis = analysisRepository.findLatestByEventId(eventId);
        if (latestAnalysis.isPresent() && !latestAnalysis.get().isOutdatedFor(event.getUpdatedAt())) {
            log.warn("manual analysis automation skipped because event already has current analysis: eventId={}", eventId);
            return new AutomationRunResult(1, 0, 0, 1, List.of());
        }

        try {
            generateAnalysisUseCase.execute(new GenerateAnalysisCommand(eventId, latestAnalysis.isPresent() ? AnalysisGenerationTrigger.REANALYSIS : AnalysisGenerationTrigger.MANUAL));
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

    private boolean isEligibleForAutomaticAnalysis(EventAnalysisCandidate candidate) {
        Event event = candidate.event();
        Optional<EventAIAnalysis> latestAnalysis = candidate.latestAnalysis();
        if (latestAnalysis.isPresent() && !latestAnalysis.get().isOutdatedFor(event.getUpdatedAt())) {
            return false;
        }

        if (event.getImportance() == Importance.CRITICAL || event.getImportance() == Importance.HIGH) {
            return true;
        }

        if (!isStable(event)) {
            return false;
        }

        if (event.getImportance() == Importance.LOW) {
            return event.getNewsIds().size() >= LOW_IMPORTANCE_MIN_NEWS;
        }

        return true;
    }

    private boolean isStable(Event event) {
        return !event.getLastUpdatedAt().isAfter(java.time.OffsetDateTime.now().minusMinutes(STANDARD_STABILIZATION_MINUTES));
    }

    private Comparator<EventAnalysisCandidate> candidateComparator() {
        return Comparator
                .comparingInt((EventAnalysisCandidate candidate) -> importanceRank(candidate.event().getImportance()))
                .thenComparing((EventAnalysisCandidate candidate) -> candidate.event().getNewsIds().size(), Comparator.reverseOrder())
                .thenComparing((EventAnalysisCandidate candidate) -> candidate.event().getLastUpdatedAt(), Comparator.reverseOrder())
                .thenComparing(candidate -> candidate.event().getId(), Comparator.reverseOrder());
    }

    private int importanceRank(Importance importance) {
        return switch (importance) {
            case CRITICAL -> 0;
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }

    private AnalysisGenerationTrigger triggerFor(EventAnalysisCandidate candidate) {
        if (candidate.latestAnalysis().isPresent()) {
            return AnalysisGenerationTrigger.REANALYSIS;
        }
        Importance importance = candidate.event().getImportance();
        if (importance == Importance.CRITICAL || importance == Importance.HIGH) {
            return AnalysisGenerationTrigger.PRIORITY_AUTO;
        }
        return AnalysisGenerationTrigger.BATCH;
    }

    private record EventAnalysisCandidate(Event event, Optional<EventAIAnalysis> latestAnalysis) {
    }
}
