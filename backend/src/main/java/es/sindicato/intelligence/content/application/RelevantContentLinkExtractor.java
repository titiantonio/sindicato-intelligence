package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.news.domain.NewsArticle;

import java.util.List;

public interface RelevantContentLinkExtractor {

    List<RelevantContentLink> extract(List<NewsArticle> newsArticles);
}
