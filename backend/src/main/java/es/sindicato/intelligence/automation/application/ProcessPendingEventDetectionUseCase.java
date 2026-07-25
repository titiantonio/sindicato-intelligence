package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import es.sindicato.intelligence.event.application.DetectEventCommand;
import es.sindicato.intelligence.event.application.DetectEventUseCase;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessPendingEventDetectionUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPendingEventDetectionUseCase.class);

    private final NewsRepository newsRepository;
    private final DetectEventUseCase detectEventUseCase;
    private final AiOperationMetricRepository aiOperationMetricRepository;
    private final int defaultLimit;
    private final int repeatedFailureThreshold;
    private final int repeatedFailureWindowHours;
    private final int lookaheadMultiplier;

    @Autowired
    public ProcessPendingEventDetectionUseCase(
            NewsRepository newsRepository,
            DetectEventUseCase detectEventUseCase,
            AiOperationMetricRepository aiOperationMetricRepository,
            @Value("${app.automation.event-detection.batch-size:10}") int defaultLimit,
            @Value("${app.automation.event-detection.repeated-failure-threshold:5}") int repeatedFailureThreshold,
            @Value("${app.automation.event-detection.repeated-failure-window-hours:24}") int repeatedFailureWindowHours,
            @Value("${app.automation.event-detection.lookahead-multiplier:3}") int lookaheadMultiplier
    ) {
        this.newsRepository = newsRepository;
        this.detectEventUseCase = detectEventUseCase;
        this.aiOperationMetricRepository = aiOperationMetricRepository;
        this.defaultLimit = defaultLimit;
        this.repeatedFailureThreshold = Math.max(1, repeatedFailureThreshold);
        this.repeatedFailureWindowHours = Math.max(1, repeatedFailureWindowHours);
        this.lookaheadMultiplier = Math.max(1, lookaheadMultiplier);
    }

    public AutomationRunResult execute() {
        return execute(defaultLimit);
    }

    public AutomationRunResult execute(int limit) {
        int effectiveLimit = Math.max(1, limit);
        List<NewsArticle> pendingNews = pendingNews(effectiveLimit);
        List<AutomationRunError> errors = new ArrayList<>();
        int successCount = 0;
        int skippedCount = 0;
        int processedCount = 0;
        int aiAttemptCount = 0;

        log.info("pending event detection automation started: limit={}, pendingCount={}", effectiveLimit, pendingNews.size());

        for (NewsArticle newsArticle : pendingNews) {
            if (aiAttemptCount >= effectiveLimit) {
                break;
            }

            try {
                long recentFailures = recentFailureCount(newsArticle.getId());
                if (recentFailures >= repeatedFailureThreshold) {
                    skippedCount++;
                    processedCount++;
                    log.warn(
                            "pending event detection item skipped because repeated failures threshold was reached: newsId={}, recentFailures={}, threshold={}, windowHours={}",
                            newsArticle.getId(),
                            recentFailures,
                            repeatedFailureThreshold,
                            repeatedFailureWindowHours
                    );
                    continue;
                }

                detectEventUseCase.execute(new DetectEventCommand(newsArticle.getId()));
                aiAttemptCount++;
                processedCount++;
                successCount++;
            } catch (RuntimeException exception) {
                aiAttemptCount++;
                processedCount++;
                errors.add(new AutomationRunError(newsArticle.getId(), truncate(exception.getMessage())));
                log.warn("pending event detection item failed: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
            }
        }

        AutomationRunResult result = new AutomationRunResult(
                processedCount,
                successCount,
                errors.size(),
                skippedCount,
                List.copyOf(errors)
        );

        log.info("pending event detection automation completed: processed={}, success={}, failed={}, skipped={}",
                result.processedCount(), result.successCount(), result.failedCount(), result.skippedCount());
        return result;
    }

    private List<NewsArticle> pendingNews(int effectiveLimit) {
        int lookaheadLimit = Math.max(effectiveLimit, effectiveLimit * lookaheadMultiplier);
        return newsRepository.findByStatus(NewsStatus.CLASSIFIED, lookaheadLimit);
    }

    private long recentFailureCount(Long newsId) {
        return aiOperationMetricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(
                "WF03_EVENT_MATCHING",
                "NEWS",
                newsId,
                AiMetricStatus.FAILED,
                OffsetDateTime.now().minusHours(repeatedFailureWindowHours)
        );
    }

    private String truncate(String message) {
        if (message == null || message.isBlank()) {
            return "automation item failed";
        }
        return message.length() <= 300 ? message : message.substring(0, 300);
    }
}
