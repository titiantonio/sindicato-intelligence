package es.sindicato.intelligence.news.domain;

import java.util.List;
import java.util.Optional;

public interface NewsRepository {

    NewsArticle save(NewsArticle newsArticle);

    Optional<NewsArticle> findById(Long id);

    Optional<NewsArticle> findByUrl(String url);

    Optional<NewsArticle> findByHash(String hash);

    List<NewsArticle> findAll();
}
