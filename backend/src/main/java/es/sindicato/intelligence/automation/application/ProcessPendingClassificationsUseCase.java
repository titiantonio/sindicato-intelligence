package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.ai.domain.AiMetricStatus;
import es.sindicato.intelligence.ai.domain.AiOperationMetricRepository;
import es.sindicato.intelligence.classification.application.ClassifyNewsCommand;
import es.sindicato.intelligence.classification.application.ClassifyNewsUseCase;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ProcessPendingClassificationsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPendingClassificationsUseCase.class);

    private final NewsRepository newsRepository;
    private final ClassifyNewsUseCase classifyNewsUseCase;
    private final AiOperationMetricRepository aiOperationMetricRepository;
    private final int defaultLimit;
    private final int repeatedFailureThreshold;
    private final int repeatedFailureWindowHours;
    private final int priorityLookaheadMultiplier;

    @Autowired
    public ProcessPendingClassificationsUseCase(
            NewsRepository newsRepository,
            ClassifyNewsUseCase classifyNewsUseCase,
            AiOperationMetricRepository aiOperationMetricRepository,
            @Value("${app.automation.classification.batch-size:10}") int defaultLimit,
            @Value("${app.automation.classification.repeated-failure-threshold:5}") int repeatedFailureThreshold,
            @Value("${app.automation.classification.repeated-failure-window-hours:24}") int repeatedFailureWindowHours,
            @Value("${app.automation.classification.priority-lookahead-multiplier:3}") int priorityLookaheadMultiplier
    ) {
        this.newsRepository = newsRepository;
        this.classifyNewsUseCase = classifyNewsUseCase;
        this.aiOperationMetricRepository = aiOperationMetricRepository;
        this.defaultLimit = defaultLimit;
        this.repeatedFailureThreshold = Math.max(1, repeatedFailureThreshold);
        this.repeatedFailureWindowHours = Math.max(1, repeatedFailureWindowHours);
        this.priorityLookaheadMultiplier = Math.max(1, priorityLookaheadMultiplier);
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

        log.info("pending classification automation started: limit={}, pendingCount={}", effectiveLimit, pendingNews.size());

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
                            "pending classification item skipped because repeated failures threshold was reached: newsId={}, recentFailures={}, threshold={}, windowHours={}",
                            newsArticle.getId(),
                            recentFailures,
                            repeatedFailureThreshold,
                            repeatedFailureWindowHours
                    );
                    continue;
                }

                classifyNewsUseCase.execute(new ClassifyNewsCommand(newsArticle.getId()));
                aiAttemptCount++;
                processedCount++;
                successCount++;
            } catch (RuntimeException exception) {
                aiAttemptCount++;
                processedCount++;
                errors.add(new AutomationRunError(newsArticle.getId(), truncate(exception.getMessage())));
                log.warn("pending classification item failed: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
            }
        }

        AutomationRunResult result = new AutomationRunResult(
                processedCount,
                successCount,
                errors.size(),
                skippedCount,
                List.copyOf(errors)
        );

        log.info("pending classification automation completed: processed={}, success={}, failed={}, skipped={}",
                result.processedCount(), result.successCount(), result.failedCount(), result.skippedCount());
        return result;
    }

    private List<NewsArticle> pendingNews(int effectiveLimit) {
        int lookaheadLimit = Math.max(effectiveLimit, effectiveLimit * priorityLookaheadMultiplier);
        return newsRepository.findByStatus(NewsStatus.CAPTURED, lookaheadLimit).stream()
                .sorted(priorityComparator())
                .toList();
    }

    private Comparator<NewsArticle> priorityComparator() {
        return Comparator
                .comparingInt((NewsArticle newsArticle) -> classifyNewsUseCase.prioritySignals(newsArticle).size()).reversed()
                .thenComparing(NewsArticle::getCapturedAt)
                .thenComparing(NewsArticle::getId);
    }

    private long recentFailureCount(Long newsId) {
        return aiOperationMetricRepository.countByPromptKeyAndRelatedEntityAndStatusSince(
                "WF02_CLASSIFICATION",
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
