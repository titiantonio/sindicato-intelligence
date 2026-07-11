package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RestoreDiscardedNewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(RestoreDiscardedNewsUseCase.class);

    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;
    private final EventRepository eventRepository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public RestoreDiscardedNewsUseCase(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            EventRepository eventRepository,
            RecordAuditLogUseCase recordAuditLogUseCase
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.eventRepository = eventRepository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public NewsArticle execute(Long newsId) {
        Objects.requireNonNull(newsId, "newsId is required");

        NewsArticle newsArticle = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException(newsId));
        NewsStatus previousStatus = newsArticle.getProcessingStatus();
        if (previousStatus != NewsStatus.DISCARDED) {
            throw new IllegalArgumentException("only discarded news can be restored");
        }

        NewsStatus restoredStatus = restoredStatus(newsId);
        applyStatus(newsArticle, restoredStatus);
        NewsArticle savedNews = newsRepository.save(newsArticle);

        recordAuditLogUseCase.record(
                "NEWS_RESTORED",
                "NEWS",
                savedNews.getId(),
                "Estado anterior: " + previousStatus,
                AuditDetailFormatter.newsRestored(savedNews.getId(), savedNews.getTitle(), previousStatus, savedNews.getProcessingStatus())
        );

        log.info("news manual discard restored: newsId={}, previousStatus={}, currentStatus={}", savedNews.getId(), previousStatus, savedNews.getProcessingStatus());
        return savedNews;
    }

    private NewsStatus restoredStatus(Long newsId) {
        if (eventRepository.existsNewsAssociation(newsId)) {
            return NewsStatus.EVENT_MATCHED;
        }
        if (classificationRepository.existsByNewsId(newsId)) {
            return NewsStatus.CLASSIFIED;
        }
        return NewsStatus.CAPTURED;
    }

    private void applyStatus(NewsArticle newsArticle, NewsStatus status) {
        if (status == NewsStatus.EVENT_MATCHED) {
            newsArticle.markEventMatched();
        } else if (status == NewsStatus.CLASSIFIED) {
            newsArticle.markClassified();
        } else {
            newsArticle.markCaptured();
        }
    }
}
