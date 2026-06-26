package es.sindicato.intelligence.event.infrastructure;

import es.sindicato.intelligence.event.application.EventEditorialStatus;
import es.sindicato.intelligence.event.application.EventSummaryQueryRepository;
import es.sindicato.intelligence.event.application.EventSummaryView;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;

@Repository
public class JpaEventSummaryQueryRepository implements EventSummaryQueryRepository {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Europe/Madrid");

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

    private final EntityManager entityManager;

    public JpaEventSummaryQueryRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<EventSummaryView> findVisibleSummaries() {
        Query query = entityManager.createNativeQuery("""
                SELECT
                    event.id,
                    event.title,
                    event.description,
                    event.category,
                    event.importance,
                    event.status,
                    CASE
                        WHEN event.manual_discarded = TRUE THEN 'DISCARDED'
                        WHEN EXISTS (
                            SELECT 1
                            FROM generated_content content
                            WHERE content.event_id = event.id
                              AND content.status = 'PUBLISHED'
                        ) THEN 'PUBLISHED'
                        WHEN EXISTS (
                            SELECT 1
                            FROM event_ai_analysis analysis
                            WHERE analysis.event_id = event.id
                        ) THEN 'ANALYZED'
                        ELSE 'PENDING_ANALYSIS'
                    END AS editorial_status,
                    COUNT(event_news.news_id) AS news_count,
                    event.first_detected_at,
                    event.last_updated_at,
                    event.updated_at
                FROM events event
                JOIN event_news event_news ON event_news.event_id = event.id
                WHERE %s
                GROUP BY event.id
                ORDER BY
                    CASE event.importance
                        WHEN 'CRITICAL' THEN 0
                        WHEN 'HIGH' THEN 1
                        WHEN 'MEDIUM' THEN 2
                        ELSE 3
                    END ASC,
                    COUNT(event_news.news_id) DESC,
                    event.last_updated_at DESC,
                    event.id DESC
                """.formatted(VISIBLE_EVENT));

        return rows(query).stream().map(this::toView).toList();
    }

    @SuppressWarnings("unchecked")
    private List<Object[]> rows(Query query) {
        return query.getResultList();
    }

    private EventSummaryView toView(Object[] row) {
        return new EventSummaryView(
                number(row[0]).longValue(),
                (String) row[1],
                (String) row[2],
                EventCategory.valueOf((String) row[3]),
                Importance.valueOf((String) row[4]),
                EventStatus.valueOf((String) row[5]),
                EventEditorialStatus.valueOf((String) row[6]),
                number(row[7]).intValue(),
                offsetDateTime(row[8]),
                offsetDateTime(row[9]),
                offsetDateTime(row[10])
        );
    }

    private Number number(Object value) {
        return (Number) value;
    }

    private OffsetDateTime offsetDateTime(Object value) {
        if (value instanceof OffsetDateTime offsetDateTime) {
            return offsetDateTime;
        }
        if (value instanceof Timestamp timestamp) {
            return timestamp.toInstant().atZone(BUSINESS_ZONE).toOffsetDateTime();
        }
        if (value instanceof Instant instant) {
            return instant.atZone(BUSINESS_ZONE).toOffsetDateTime();
        }
        if (value instanceof LocalDateTime localDateTime) {
            return localDateTime.atZone(BUSINESS_ZONE).toOffsetDateTime();
        }
        throw new IllegalArgumentException("Unsupported timestamp value: " + value);
    }
}
