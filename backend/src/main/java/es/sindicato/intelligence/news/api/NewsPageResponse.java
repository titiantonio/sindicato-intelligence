package es.sindicato.intelligence.news.api;

import java.util.List;

public record NewsPageResponse(
        List<NewsPageItemResponse> items,
        int page,
        int pageSize,
        long totalItems,
        int totalPages
) {
}
