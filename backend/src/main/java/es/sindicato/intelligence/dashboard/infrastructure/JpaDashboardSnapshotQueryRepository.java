package es.sindicato.intelligence.dashboard.infrastructure;

import es.sindicato.intelligence.dashboard.application.DashboardSnapshotQueryRepository;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase.DashboardLastUpdated;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase.DashboardMetric;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase.DashboardSnapshot;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase.DashboardTotals;
import es.sindicato.intelligence.dashboard.application.DashboardSnapshotUseCase.PriorityEventView;
import es.sindicato.intelligence.event.application.EventEditorialStatus;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Repository
public class JpaDashboardSnapshotQueryRepository implements DashboardSnapshotQueryRepository {

    private static final ZoneId DASHBOARD_ZONE = ZoneId.of("Europe/Madrid");
    private static final int PRIORITY_EVENTS_LIMIT = 10;

    private static final String VISIBLE_NEWS = """
            news.processing_status <> 'DISCARDED'
            AND NOT EXISTS (
                SELECT 1
                FROM news_classifications classification
                WHERE classification.news_id = news.id
                  AND classification.category = 'OTROS'
                  AND classification.relevance_score = 0
                  AND UPPER(TRIM(classification.subcategory)) IN ('FUERA_DE_AMBITO', 'INFORMACION_INSUFICIENTE')
            )
            """;

    private static final String VISIBLE_EVENT = """
            event.status <> 'ARCHIVED'
            AND EXISTS (
                SELECT 1
                FROM event_news event_news
                JOIN news_articles news ON news.id = event_news.news_id
                WHERE event_news.event_id = event.id
                  AND %s
            )
            """.formatted(VISIBLE_NEWS);

    private static final String VISIBLE_CONTENT = """
            EXISTS (
                SELECT 1
                FROM events event
                WHERE event.id = content.event_id
                  AND %s
            )
            """.formatted(VISIBLE_EVENT);

    private static final String VISIBLE_PUBLICATION = """
            EXISTS (
                SELECT 1
                FROM generated_content content
                JOIN events event ON event.id = content.event_id
                WHERE content.id = publication.content_id
                  AND %s
            )
            """.formatted(VISIBLE_EVENT);

    private final EntityManager entityManager;
    private final Clock clock;

    public JpaDashboardSnapshotQueryRepository(EntityManager entityManager, Clock clock) {
        this.entityManager = entityManager;
        this.clock = clock;
    }

    @Override
    public DashboardSnapshot loadSnapshot() {
        DateRange today = todayRange();
        DateRange yesterday = new DateRange(today.start().minusDays(1), today.start());

        DashboardMetric capturedNews = metric(
                "capturedNews",
                count("""
                        SELECT COUNT(*)
                        FROM news_articles news
                        WHERE %s
                          AND news.captured_at >= :from
                          AND news.captured_at < :to
                        """.formatted(VISIBLE_NEWS), today),
                count("""
                        SELECT COUNT(*)
                        FROM news_articles news
                        WHERE %s
                          AND news.captured_at >= :from
                          AND news.captured_at < :to
                        """.formatted(VISIBLE_NEWS), yesterday)
        );
        DashboardMetric detectedEvents = metric(
                "detectedEvents",
                count("""
                        SELECT COUNT(*)
                        FROM events event
                        WHERE %s
                          AND event.first_detected_at >= :from
                          AND event.first_detected_at < :to
                        """.formatted(VISIBLE_EVENT), today),
                count("""
                        SELECT COUNT(*)
                        FROM events event
                        WHERE %s
                          AND event.first_detected_at >= :from
                          AND event.first_detected_at < :to
                        """.formatted(VISIBLE_EVENT), yesterday)
        );
        DashboardMetric pendingContents = metric(
                "pendingContents",
                count("""
                        SELECT COUNT(*)
                        FROM generated_content content
                        WHERE content.status = 'PENDING_REVIEW'
                          AND %s
                          AND content.generated_at >= :from
                          AND content.generated_at < :to
                        """.formatted(VISIBLE_CONTENT), today),
                count("""
                        SELECT COUNT(*)
                        FROM generated_content content
                        WHERE content.status = 'PENDING_REVIEW'
                          AND %s
                          AND content.generated_at >= :from
                          AND content.generated_at < :to
                        """.formatted(VISIBLE_CONTENT), yesterday)
        );
        DashboardMetric publishedPublications = metric(
                "publishedPublications",
                count("""
                        SELECT COUNT(*)
                        FROM publications publication
                        WHERE publication.publication_status = 'PUBLISHED'
                          AND publication.published_at IS NOT NULL
                          AND %s
                          AND publication.published_at >= :from
                          AND publication.published_at < :to
                        """.formatted(VISIBLE_PUBLICATION), today),
                count("""
                        SELECT COUNT(*)
                        FROM publications publication
                        WHERE publication.publication_status = 'PUBLISHED'
                          AND publication.published_at IS NOT NULL
                          AND %s
                          AND publication.published_at >= :from
                          AND publication.published_at < :to
                        """.formatted(VISIBLE_PUBLICATION), yesterday)
        );

        DashboardTotals totals = new DashboardTotals(
                count("""
                        SELECT COUNT(*)
                        FROM news_articles news
                        WHERE %s
                        """.formatted(VISIBLE_NEWS)),
                count("""
                        SELECT COUNT(*)
                        FROM events event
                        WHERE %s
                          AND event.status IN ('OPEN', 'MONITORING')
                          AND event.importance = 'CRITICAL'
                        """.formatted(VISIBLE_EVENT)),
                countContentStatus("PENDING_REVIEW"),
                countContentStatus("GENERATED"),
                countContentStatus("APPROVED"),
                countPublicationStatus("SCHEDULED"),
                countPublicationStatus("FAILED")
        );

        DashboardLastUpdated lastUpdated = new DashboardLastUpdated(
                latest("""
                        SELECT MAX(GREATEST(news.updated_at, news.captured_at))
                        FROM news_articles news
                        WHERE %s
                        """.formatted(VISIBLE_NEWS)),
                latest("""
                        SELECT MAX(GREATEST(event.updated_at, event.last_updated_at))
                        FROM events event
                        WHERE %s
                        """.formatted(VISIBLE_EVENT)),
                latest("""
                        SELECT MAX(GREATEST(content.generated_at, COALESCE(content.approved_at, content.generated_at)))
                        FROM generated_content content
                        WHERE %s
                        """.formatted(VISIBLE_CONTENT)),
                latest("""
                        SELECT MAX(GREATEST(
                            COALESCE(publication.published_at, publication.scheduled_at),
                            COALESCE(publication.scheduled_at, publication.published_at)
                        ))
                        FROM publications publication
                        WHERE %s
                          AND (publication.published_at IS NOT NULL OR publication.scheduled_at IS NOT NULL)
                        """.formatted(VISIBLE_PUBLICATION))
        );

        return new DashboardSnapshot(
                capturedNews,
                detectedEvents,
                pendingContents,
                publishedPublications,
                totals,
                lastUpdated,
                priorityEvents()
        );
    }

    private DashboardMetric metric(String key, long todayValue, long yesterdayValue) {
        return new DashboardMetric(key, todayValue, yesterdayValue, todayValue - yesterdayValue);
    }

    private long countContentStatus(String status) {
        Query query = entityManager.createNativeQuery("""
                SELECT COUNT(*)
                FROM generated_content content
                WHERE content.status = :status
                  AND %s
                """.formatted(VISIBLE_CONTENT));
        query.setParameter("status", status);
        return number(query.getSingleResult()).longValue();
    }

    private long countPublicationStatus(String status) {
        Query query = entityManager.createNativeQuery("""
                SELECT COUNT(*)
                FROM publications publication
                WHERE publication.publication_status = :status
                  AND %s
                """.formatted(VISIBLE_PUBLICATION));
        query.setParameter("status", status);
        return number(query.getSingleResult()).longValue();
    }

    private long count(String sql) {
        return number(entityManager.createNativeQuery(sql).getSingleResult()).longValue();
    }

    private long count(String sql, DateRange range) {
        Query query = entityManager.createNativeQuery(sql);
        query.setParameter("from", range.start());
        query.setParameter("to", range.end());
        return number(query.getSingleResult()).longValue();
    }

    private OffsetDateTime latest(String sql) {
        Object value = entityManager.createNativeQuery(sql).getSingleResult();
        if (value == null) {
            return OffsetDateTime.now(clock.withZone(DASHBOARD_ZONE));
        }
        return offsetDateTime(value);
    }

    private List<PriorityEventView> priorityEvents() {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    event.id,
                    event.title,
                    event.category,
                    event.importance,
                    COUNT(event_news.news_id) AS news_count,
                    event.last_updated_at,
                    event.status
                FROM events event
                JOIN event_news event_news ON event_news.event_id = event.id
                WHERE %s
                  AND event.status IN ('OPEN', 'MONITORING')
                  AND event.manual_discarded = FALSE
                  AND event.category <> 'OTROS'
                  AND event.importance IN ('HIGH', 'CRITICAL')
                  AND NOT EXISTS (
                      SELECT 1
                      FROM event_ai_analysis analysis
                      WHERE analysis.event_id = event.id
                  )
                  AND NOT EXISTS (
                      SELECT 1
                      FROM generated_content content
                      WHERE content.event_id = event.id
                        AND content.status = 'PUBLISHED'
                  )
                GROUP BY event.id
                ORDER BY
                    CASE event.importance WHEN 'CRITICAL' THEN 0 ELSE 1 END ASC,
                    COUNT(event_news.news_id) DESC,
                    event.last_updated_at DESC,
                    event.id DESC
                LIMIT :limit
                """.formatted(VISIBLE_EVENT));
        query.setParameter("limit", PRIORITY_EVENTS_LIMIT);
        return rows(query).stream().map(this::toPriorityEvent).toList();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Query query) {
        return query.getResultList();
    }

    private PriorityEventView toPriorityEvent(Object[] row) {
        return new PriorityEventView(
                number(row[0]).longValue(),
                (String) row[1],
                EventCategory.valueOf((String) row[2]),
                Importance.valueOf((String) row[3]),
                number(row[4]).intValue(),
                offsetDateTime(row[5]),
                EventStatus.valueOf((String) row[6]),
                EventEditorialStatus.PENDING_ANALYSIS
        );
    }

    private DateRange todayRange() {
        LocalDate today = LocalDate.now(clock.withZone(DASHBOARD_ZONE));
        OffsetDateTime start = today.atStartOfDay(DASHBOARD_ZONE).toOffsetDateTime();
        return new DateRange(start, start.plusDays(1));
    }

    private Number number(Object value) {
        return (Number) value;
    }

    private OffsetDateTime offsetDateTime(Object value) {
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant().atZone(DASHBOARD_ZONE).toOffsetDateTime();
        }
        if (value instanceof Instant instant) {
            return instant.atZone(DASHBOARD_ZONE).toOffsetDateTime();
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.atZone(DASHBOARD_ZONE).toOffsetDateTime();
        }
        throw new IllegalArgumentException("Unsupported timestamp value: " + value);
    }

    private record DateRange(OffsetDateTime start, OffsetDateTime end) {
    }
}
