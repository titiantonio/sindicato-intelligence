package es.sindicato.intelligence.news.application;

import es.sindicato.intelligence.news.domain.NewsArticle;
import es.sindicato.intelligence.news.domain.NewsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListNewsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListNewsUseCase.class);

    private final NewsRepository newsRepository;

    public ListNewsUseCase(NewsRepository newsRepository) {
        this.newsRepository = newsRepository;
    }

    @Transactional(readOnly = true)
    public List<NewsArticle> execute() {
        List<NewsArticle> newsArticles = newsRepository.findAll();
        log.info("news listed: count={}", newsArticles.size());

        return newsArticles;
    }
}
