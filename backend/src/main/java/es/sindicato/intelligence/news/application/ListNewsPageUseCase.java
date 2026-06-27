package es.sindicato.intelligence.news.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Set;

@Service
public class ListNewsPageUseCase {

    private static final Logger log = LoggerFactory.getLogger(ListNewsPageUseCase.class);
    private static final int DEFAULT_PAGE = 1;
    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final int MAX_PAGE_SIZE = 100;
    private static final Set<String> SORT_COLUMNS = Set.of(
            "id",
            "title",
            "sourceId",
            "processingStatus",
            "eventId",
            "category",
            "publishedAt",
            "capturedAt"
    );
    private static final Map<String, String> SORT_COLUMN_ALIASES = Map.of(
            "source_id", "sourceId",
            "processing_status", "processingStatus",
            "event_id", "eventId",
            "published_at", "publishedAt",
            "captured_at", "capturedAt"
    );

    private final NewsPageQueryRepository repository;

    public ListNewsPageUseCase(NewsPageQueryRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public NewsPage execute(NewsPageQuery request) {
        NewsPageQuery query = sanitize(request);
        NewsPage page = repository.findPage(query);
        log.info(
                "news page listed: page={} pageSize={} totalItems={} sortColumn={} sortDirection={}",
                page.page(),
                page.pageSize(),
                page.totalItems(),
                query.sortColumn(),
                query.sortDirection()
        );
        return page;
    }

    private NewsPageQuery sanitize(NewsPageQuery query) {
        int page = query == null ? DEFAULT_PAGE : Math.max(DEFAULT_PAGE, query.page());
        int pageSize = query == null ? DEFAULT_PAGE_SIZE : query.pageSize();
        if (pageSize <= 0) {
            pageSize = DEFAULT_PAGE_SIZE;
        }
        pageSize = Math.min(pageSize, MAX_PAGE_SIZE);

        String sortColumn = query == null ? "capturedAt" : normalizeSortColumn(query.sortColumn());
        String sortDirection = query == null ? "desc" : normalizeSortDirection(query.sortDirection());

        return new NewsPageQuery(
                page,
                pageSize,
                clean(query == null ? null : query.global()),
                clean(query == null ? null : query.id()),
                clean(query == null ? null : query.title()),
                clean(query == null ? null : query.source()),
                clean(query == null ? null : query.status()),
                clean(query == null ? null : query.event()),
                clean(query == null ? null : query.category()),
                clean(query == null ? null : query.publishedAt()),
                clean(query == null ? null : query.capturedAt()),
                sortColumn,
                sortDirection
        );
    }

    private String normalizeSortColumn(String value) {
        if (value == null || value.isBlank()) {
            return "capturedAt";
        }

        String trimmed = value.trim();
        String aliased = SORT_COLUMN_ALIASES.getOrDefault(trimmed, trimmed);
        return SORT_COLUMNS.contains(aliased) ? aliased : "capturedAt";
    }

    private String normalizeSortDirection(String value) {
        if (value == null) {
            return "desc";
        }

        return "asc".equalsIgnoreCase(value.trim()) ? "asc" : "desc";
    }

    private String clean(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }

        return value.trim();
    }
}
