package es.sindicato.intelligence.news.infrastructure;

import es.sindicato.intelligence.news.application.NewsPage;
import es.sindicato.intelligence.news.application.NewsPageItem;
import es.sindicato.intelligence.news.application.NewsPageQuery;
import es.sindicato.intelligence.news.application.NewsPageQueryRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Repository
public class JpaNewsPageQueryRepository implements NewsPageQueryRepository {

    private static final Map<String, String> SORT_COLUMNS = Map.of(
            "id", "news.id",
            "title", "news.title",
            "sourceId", "sources.name",
            "processingStatus", "news.processing_status",
            "eventId", "event_news.event_id",
            "category", "classification.category",
            "publishedAt", "news.published_at",
            "capturedAt", "news.captured_at"
    );

    private final EntityManager entityManager;

    public JpaNewsPageQueryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public NewsPage findPage(NewsPageQuery query) {
        String whereClause = buildWhereClause(query);
        String orderClause = buildOrderClause(query);
        int offset = (query.page() - 1) * query.pageSize();

        Query itemsQuery = entityManager.createNativeQuery("""
                SELECT
                    news.id,
                    news.source_id,
                    sources.name,
                    news.title,
                    news.processing_status,
                    event_news.event_id,
                    classification.category,
                    news.published_at,
                    news.captured_at
                FROM news_articles news
                INNER JOIN sources sources ON sources.id = news.source_id
                LEFT JOIN event_news event_news ON event_news.news_id = news.id
                LEFT JOIN news_classifications classification ON classification.news_id = news.id
                %s
                %s
                LIMIT :limit OFFSET :offset
                """.formatted(whereClause, orderClause));
        applyParameters(itemsQuery, query);
        itemsQuery.setParameter("limit", query.pageSize());
        itemsQuery.setParameter("offset", offset);

        Query countQuery = entityManager.createNativeQuery("""
                SELECT COUNT(*)
                FROM news_articles news
                INNER JOIN sources sources ON sources.id = news.source_id
                LEFT JOIN event_news event_news ON event_news.news_id = news.id
                LEFT JOIN news_classifications classification ON classification.news_id = news.id
                %s
                """.formatted(whereClause));
        applyParameters(countQuery, query);

        List<NewsPageItem> items = toItems(itemsQuery.getResultList());
        long totalItems = ((Number) countQuery.getSingleResult()).longValue();
        int totalPages = Math.max(1, (int) Math.ceil((double) totalItems / query.pageSize()));

        return new NewsPage(items, query.page(), query.pageSize(), totalItems, totalPages);
    }

    private String buildWhereClause(NewsPageQuery query) {
        List<String> filters = new ArrayList<>();
        addFilter(filters, query.global(), """
                (
                    CAST(news.id AS TEXT) ILIKE :global
                    OR ('#' || CAST(news.id AS TEXT)) ILIKE :global
                    OR news.title ILIKE :global
                    OR sources.name ILIKE :global
                    OR ('Fuente #' || CAST(news.source_id AS TEXT)) ILIKE :global
                    OR CAST(news.source_id AS TEXT) ILIKE :global
                    OR news.processing_status ILIKE :global
                    OR COALESCE('#' || CAST(event_news.event_id AS TEXT), 'Sin evento') ILIKE :global
                    OR COALESCE(classification.category, 'Sin clasificar') ILIKE :global
                    OR COALESCE(TO_CHAR(news.published_at, 'DD/MM/YYYY HH24:MI'), '-') ILIKE :global
                    OR TO_CHAR(news.captured_at, 'DD/MM/YYYY HH24:MI') ILIKE :global
                )
                """);
        addFilter(filters, query.id(), "(CAST(news.id AS TEXT) ILIKE :id OR ('#' || CAST(news.id AS TEXT)) ILIKE :id)");
        addFilter(filters, query.title(), "news.title ILIKE :title");
        addFilter(filters, query.source(), "(sources.name ILIKE :source OR ('Fuente #' || CAST(news.source_id AS TEXT)) ILIKE :source OR CAST(news.source_id AS TEXT) ILIKE :source)");
        addFilter(filters, query.status(), "news.processing_status = :statusExact");
        addFilter(filters, query.event(), "COALESCE('#' || CAST(event_news.event_id AS TEXT), 'Sin evento') ILIKE :event");
        addFilter(filters, query.category(), "COALESCE(classification.category, 'Sin clasificar') = :categoryExact");
        addFilter(filters, query.publishedAt(), "COALESCE(TO_CHAR(news.published_at, 'DD/MM/YYYY HH24:MI'), '-') ILIKE :publishedAt");
        addFilter(filters, query.capturedAt(), "TO_CHAR(news.captured_at, 'DD/MM/YYYY HH24:MI') ILIKE :capturedAt");

        if (filters.isEmpty()) {
            return "";
        }

        return "WHERE " + String.join(" AND ", filters);
    }

    private void addFilter(List<String> filters, String value, String filter) {
        if (value != null) {
            filters.add(filter);
        }
    }

    private String buildOrderClause(NewsPageQuery query) {
        String column = SORT_COLUMNS.getOrDefault(query.sortColumn(), "news.captured_at");
        String direction = "asc".equalsIgnoreCase(query.sortDirection()) ? "ASC" : "DESC";
        return "ORDER BY " + column + " " + direction + " NULLS LAST, news.id " + direction;
    }

    private void applyParameters(Query jpaQuery, NewsPageQuery query) {
        setLike(jpaQuery, "global", query.global());
        setLike(jpaQuery, "id", query.id());
        setLike(jpaQuery, "title", query.title());
        setLike(jpaQuery, "source", query.source());
        setExact(jpaQuery, "statusExact", query.status());
        setLike(jpaQuery, "event", query.event());
        setExact(jpaQuery, "categoryExact", query.category());
        setLike(jpaQuery, "publishedAt", query.publishedAt());
        setLike(jpaQuery, "capturedAt", query.capturedAt());
    }

    private void setLike(Query query, String name, String value) {
        if (value != null) {
            query.setParameter(name, "%" + value + "%");
        }
    }

    private void setExact(Query query, String name, String value) {
        if (value != null) {
            query.setParameter(name, value);
        }
    }

    private List<NewsPageItem> toItems(List<?> rows) {
        return rows.stream()
                .map(row -> (Object[]) row)
                .map(row -> new NewsPageItem(
                        ((Number) row[0]).longValue(),
                        ((Number) row[1]).longValue(),
                        (String) row[2],
                        (String) row[3],
                        (String) row[4],
                        row[5] == null ? null : ((Number) row[5]).longValue(),
                        (String) row[6],
                        toOffsetDateTime(row[7]),
                        toOffsetDateTime(row[8])
                ))
                .toList();
    }

    private OffsetDateTime toOffsetDateTime(Object value) {
        if (value == null) {
            return null;
        }

        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime;
        }

        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant().atOffset(ZoneOffset.UTC);
        }

        if (value instanceof Instant instant) {
            return instant.atOffset(ZoneOffset.UTC);
        }

        throw new IllegalArgumentException("unsupported timestamp value: " + value.getClass().getName());
    }
}
