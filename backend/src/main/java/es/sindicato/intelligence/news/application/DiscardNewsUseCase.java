package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class DiscardNewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(DiscardNewsUseCase.class);

    private final NewsRepository newsRepository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public DiscardNewsUseCase(NewsRepository newsRepository, RecordAuditLogUseCase recordAuditLogUseCase) {
        this.newsRepository = newsRepository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public NewsArticle execute(Long newsId) {
        Objects.requireNonNull(newsId, "newsId is required");

        NewsArticle newsArticle = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException(newsId));
        NewsStatus previousStatus = newsArticle.getProcessingStatus();
        if (previousStatus == NewsStatus.ARCHIVED) {
            throw new IllegalArgumentException("archived news cannot be manually discarded");
        }

        newsArticle.markDiscarded();
        NewsArticle savedNews = newsRepository.save(newsArticle);

        recordAuditLogUseCase.record(
                "NEWS_DISCARDED",
                "NEWS",
                savedNews.getId(),
                "Estado anterior: " + previousStatus,
                AuditDetailFormatter.newsDiscarded(savedNews.getId(), savedNews.getTitle(), previousStatus, savedNews.getProcessingStatus())
        );

        log.info("news manually discarded: newsId={}, previousStatus={}, currentStatus={}", savedNews.getId(), previousStatus, savedNews.getProcessingStatus());
        return savedNews;
    }
}
