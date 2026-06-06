package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class GetNewsUseCase {

    private final NewsRepository newsRepository;

    public GetNewsUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    public NewsArticle execute(Long id) {
        Objects.requireNonNull(id, "id is required");

        return newsRepository.findById(id)
                .orElseThrow(() -> new NewsNotFoundException(id));
    }
}
