package es.sindicato.intelligence.news.application;

public interface NewsPageQueryRepository {

    NewsPage findPage(NewsPageQuery query);
}
