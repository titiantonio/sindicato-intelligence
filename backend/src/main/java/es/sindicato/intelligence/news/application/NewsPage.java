package es.sindicato.intelligence.news.application;

import java.util.List;

public record NewsPage(
        List<NewsPageItem> items,
        int page,
        int pageSize,
        long totalItems,
        int totalPages
) {
}
