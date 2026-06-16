package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.event.application.DetectEventCommand;
import es.sindicato.intelligence.event.application.DetectEventUseCase;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProcessPendingEventDetectionUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProcessPendingEventDetectionUseCase.class);

    private final NewsRepository newsRepository;
    private final DetectEventUseCase detectEventUseCase;
    private final int defaultLimit;

    public ProcessPendingEventDetectionUseCase(
            NewsRepository newsRepository,
            DetectEventUseCase detectEventUseCase,
            @Value("${app.automation.event-detection.batch-size:10}") int defaultLimit
    ) {
        this.newsRepository = newsRepository;
        this.detectEventUseCase = detectEventUseCase;
        this.defaultLimit = defaultLimit;
    }

    public AutomationRunResult execute() {
        return execute(defaultLimit);
    }

    public AutomationRunResult execute(int limit) {
        int effectiveLimit = Math.max(1, limit);
        List<NewsArticle> pendingNews = newsRepository.findByStatus(NewsStatus.CLASSIFIED, effectiveLimit);
        List<AutomationRunError> errors = new ArrayList<>();
        int successCount = 0;

        log.info("pending event detection automation started: limit={}, pendingCount={}", effectiveLimit, pendingNews.size());

        for (NewsArticle newsArticle : pendingNews) {
            try {
                detectEventUseCase.execute(new DetectEventCommand(newsArticle.getId()));
                successCount++;
            } catch (RuntimeException exception) {
                errors.add(new AutomationRunError(newsArticle.getId(), truncate(exception.getMessage())));
                log.warn("pending event detection item failed: newsId={}, reason={}", newsArticle.getId(), exception.getMessage());
            }
        }

        AutomationRunResult result = new AutomationRunResult(
                pendingNews.size(),
                successCount,
                errors.size(),
                0,
                List.copyOf(errors)
        );

        log.info("pending event detection automation completed: processed={}, success={}, failed={}",
                result.processedCount(), result.successCount(), result.failedCount());
        return result;
    }

    private String truncate(String message) {
        if (message == null || message.isBlank()) {
            return "automation item failed";
        }
        return message.length() <= 300 ? message : message.substring(0, 300);
    }
}
