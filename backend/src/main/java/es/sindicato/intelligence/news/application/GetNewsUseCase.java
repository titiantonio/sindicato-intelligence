package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class GetNewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(GetNewsUseCase.class);

    private final NewsRepository newsRepository;

    public GetNewsUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    public NewsArticle execute(Long id) {
        Objects.requireNonNull(id, "id is required");

        NewsArticle newsArticle = newsRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("news lookup failed because news does not exist: newsId={}", id);
                    return new NewsNotFoundException(id);
                });
        log.info("news retrieved: newsId={}, sourceId={}, status={}", newsArticle.getId(), newsArticle.getSourceId(), newsArticle.getProcessingStatus());

        return newsArticle;
    }
}
