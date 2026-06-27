package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.classification.domain.NewsClassification;
import es.sindicato.intelligence.classification.domain.NewsClassificationRepository;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import es.sindicato.intelligence.source.domain.Source;
import es.sindicato.intelligence.source.domain.SourceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class GetNewsTraceUseCase {

    private final NewsRepository newsRepository;
    private final EventRepository eventRepository;
    private final NewsClassificationRepository classificationRepository;
    private final SourceRepository sourceRepository;

    public GetNewsTraceUseCase(
            NewsRepository newsRepository,
            EventRepository eventRepository,
            NewsClassificationRepository classificationRepository,
            SourceRepository sourceRepository
    ) {
        this.newsRepository = newsRepository;
        this.eventRepository = eventRepository;
        this.classificationRepository = classificationRepository;
        this.sourceRepository = sourceRepository;
    }

    @Transactional(readOnly = true)
    public NewsTrace execute(Long newsId) {
        NewsArticle news = newsRepository.findById(newsId)
                .orElseThrow(() -> new NewsNotFoundException(newsId));
        Event event = eventRepository.findByNewsId(newsId).orElse(null);
        NewsClassification classification = classificationRepository.findByNewsId(newsId).orElse(null);
        Source source = sourceRepository.findById(news.getSourceId()).orElse(null);
        return new NewsTrace(news, source == null ? null : source.getName(), event, classification);
    }

    public record NewsTrace(
            NewsArticle news,
            String sourceName,
            Event event,
            NewsClassification classification
    ) {
    }
}
