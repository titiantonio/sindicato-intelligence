package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.content.domain.GeneratedContent;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GetEventDetailUseCase {

    private final EventRepository eventRepository;
    private final NewsRepository newsRepository;
    private final NewsClassificationRepository classificationRepository;
    private final EventAIAnalysisRepository analysisRepository;
    private final GeneratedContentRepository contentRepository;

    public GetEventDetailUseCase(
            EventRepository eventRepository,
            NewsRepository newsRepository,
            NewsClassificationRepository classificationRepository,
            EventAIAnalysisRepository analysisRepository,
            GeneratedContentRepository contentRepository
    ) {
        this.eventRepository = eventRepository;
        this.newsRepository = newsRepository;
        this.classificationRepository = classificationRepository;
        this.analysisRepository = analysisRepository;
        this.contentRepository = contentRepository;
    }

    @Transactional(readOnly = true)
    public EventDetail execute(Long eventId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        List<EventNewsDetail> news = event.getNewsIds().stream()
                .sorted()
                .map(this::toNewsDetail)
                .toList();

        return new EventDetail(
                event,
                news,
                analysisRepository.findByEventId(event.getId()),
                contentRepository.findByEventId(event.getId())
        );
    }

    private EventNewsDetail toNewsDetail(Long newsId) {
        NewsArticle newsArticle = newsRepository.findById(newsId)
                .orElseThrow(() -> new IllegalStateException("event references missing news: " + newsId));
        NewsClassification classification = classificationRepository.findByNewsId(newsId).orElse(null);
        return new EventNewsDetail(newsArticle, classification);
    }

    public record EventDetail(
            Event event,
            List<EventNewsDetail> news,
            List<EventAIAnalysis> analyses,
            List<GeneratedContent> contents
    ) {
    }

    public record EventNewsDetail(
            NewsArticle newsArticle,
            NewsClassification classification
    ) {
    }
}