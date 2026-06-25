package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.news.domain.NewsStatus;
import org.springframework.stereotype.Component;

@Component
public class EventVisibilityPolicy {

    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;

    public EventVisibilityPolicy(
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository
    ) {
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
    }

    public boolean isVisible(Event event) {
        if (event.getStatus() == EventStatus.ARCHIVED || event.getNewsIds().isEmpty()) {
            return false;
        }

        return event.getNewsIds().stream()
                .anyMatch(this::isVisibleNews);
    }

    public boolean isVisibleNews(NewsArticle newsArticle) {
        if (newsArticle.getProcessingStatus() == NewsStatus.DISCARDED) {
            return false;
        }

        return classificationRepository.findByNewsId(newsArticle.getId())
                .map(classification -> !classification.isDiscardableForEventDetection())
                .orElse(true);
    }

    private boolean isVisibleNews(Long newsId) {
        return newsRepository.findById(newsId)
                .map(this::isVisibleNews)
                .orElse(false);
    }
}
