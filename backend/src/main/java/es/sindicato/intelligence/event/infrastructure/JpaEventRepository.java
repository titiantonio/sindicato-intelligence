package es.sindicato.intelligence.event.infrastructure;

import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.EventMatchDecision;
import es.sindicato.intelligence.event.domain.EventNewsAssociationTrace;
import es.sindicato.intelligence.event.domain.EventNewsAssociationTraceRepository;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.EventStatus;
import es.sindicato.intelligence.event.domain.Importance;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class JpaEventRepository implements EventRepository, EventNewsAssociationTraceRepository {

    private final EntityManager entityManager;

    public JpaEventRepository(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public Event save(Event event) {
        EventEntity entity = toEntity(event);

        if (entity.getId() == null) {
            entityManager.persist(entity);
            entityManager.flush();
            syncNewsAssociations(entity.getId(), event.getNewsIds());
            return toDomain(entity);
        }

        EventEntity merged = entityManager.merge(entity);
        syncNewsAssociations(merged.getId(), event.getNewsIds());
        return toDomain(merged);
    }

    @Override
    public Optional<Event> findById(Long id) {
        return Optional.ofNullable(entityManager.find(EventEntity.class, id))
                .map(this::toDomain);
    }

    @Override
    public List<Event> findAll() {
        return entityManager.createQuery(
                        "SELECT event FROM EventEntity event ORDER BY event.lastUpdatedAt DESC, event.id DESC",
                        EventEntity.class
                )
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Event> findByStatus(EventStatus status) {
        return entityManager.createQuery(
                        "SELECT event FROM EventEntity event WHERE event.status = :status ORDER BY event.lastUpdatedAt DESC, event.id DESC",
                        EventEntity.class
                )
                .setParameter("status", status)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Event> findByCategory(EventCategory category) {
        return entityManager.createQuery(
                        "SELECT event FROM EventEntity event WHERE event.category = :category ORDER BY event.lastUpdatedAt DESC, event.id DESC",
                        EventEntity.class
                )
                .setParameter("category", category)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Event> findByImportance(Importance importance) {
        return entityManager.createQuery(
                        "SELECT event FROM EventEntity event WHERE event.importance = :importance ORDER BY event.lastUpdatedAt DESC, event.id DESC",
                        EventEntity.class
                )
                .setParameter("importance", importance)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public List<Event> findByStatusIn(List<EventStatus> statuses) {
        return entityManager.createQuery(
                        "SELECT event FROM EventEntity event WHERE event.status IN :statuses ORDER BY event.lastUpdatedAt DESC, event.id DESC",
                        EventEntity.class
                )
                .setParameter("statuses", statuses)
                .getResultList()
                .stream()
                .map(this::toDomain)
                .toList();
    }

    @Override
    public Optional<Event> findByNewsId(Long newsId) {
        return entityManager.createQuery(
                        """
                        SELECT event
                        FROM EventEntity event
                        JOIN EventNewsEntity eventNews ON eventNews.eventId = event.id
                        WHERE eventNews.newsId = :newsId
                        ORDER BY event.lastUpdatedAt DESC, event.id DESC
                        """,
                        EventEntity.class
                )
                .setParameter("newsId", newsId)
                .getResultList()
                .stream()
                .findFirst()
                .map(this::toDomain);
    }

    @Override
    public void saveNewsAssociation(Long eventId, Long newsId, Integer confidenceScore, EventMatchDecision matchDecision, String matchReason) {
        validateConfidenceScore(confidenceScore);

        Optional<EventNewsEntity> existing = findAssociation(eventId, newsId);

        if (existing.isPresent()) {
            existing.get().setConfidenceScore(confidenceScore);
            existing.get().setMatchDecision(matchDecision);
            existing.get().setMatchReason(matchReason);
            return;
        }

        entityManager.persist(new EventNewsEntity(null, eventId, newsId, confidenceScore, matchDecision, matchReason, OffsetDateTime.now()));
    }

    @Override
    public boolean existsNewsAssociation(Long newsId) {
        Long count = entityManager.createQuery(
                        "SELECT COUNT(eventNews) FROM EventNewsEntity eventNews WHERE eventNews.newsId = :newsId",
                        Long.class
                )
                .setParameter("newsId", newsId)
                .getSingleResult();

        return count > 0;
    }

    @Override
    public List<EventNewsAssociationTrace> findByEventId(Long eventId) {
        return entityManager.createQuery(
                        "SELECT eventNews FROM EventNewsEntity eventNews WHERE eventNews.eventId = :eventId ORDER BY eventNews.createdAt ASC, eventNews.id ASC",
                        EventNewsEntity.class
                )
                .setParameter("eventId", eventId)
                .getResultList()
                .stream()
                .map(eventNews -> new EventNewsAssociationTrace(
                        eventNews.getNewsId(),
                        eventNews.getConfidenceScore(),
                        eventNews.getMatchDecision(),
                        eventNews.getMatchReason()
                ))
                .toList();
    }

    private void syncNewsAssociations(Long eventId, Set<Long> newsIds) {
        Set<Long> existingNewsIds = findNewsIdsByEventId(eventId);

        for (Long newsId : newsIds) {
            if (!existingNewsIds.contains(newsId)) {
                entityManager.persist(new EventNewsEntity(null, eventId, newsId, null, OffsetDateTime.now()));
            }
        }

        for (Long existingNewsId : existingNewsIds) {
            if (!newsIds.contains(existingNewsId)) {
                findAssociation(eventId, existingNewsId).ifPresent(entityManager::remove);
            }
        }
    }

    private Optional<EventNewsEntity> findAssociation(Long eventId, Long newsId) {
        return entityManager.createQuery(
                        "SELECT eventNews FROM EventNewsEntity eventNews WHERE eventNews.eventId = :eventId AND eventNews.newsId = :newsId",
                        EventNewsEntity.class
                )
                .setParameter("eventId", eventId)
                .setParameter("newsId", newsId)
                .getResultList()
                .stream()
                .findFirst();
    }

    private Set<Long> findNewsIdsByEventId(Long eventId) {
        return entityManager.createQuery(
                        "SELECT eventNews.newsId FROM EventNewsEntity eventNews WHERE eventNews.eventId = :eventId",
                        Long.class
                )
                .setParameter("eventId", eventId)
                .getResultList()
                .stream()
                .collect(Collectors.toSet());
    }

    private EventEntity toEntity(Event event) {
        return new EventEntity(
                event.getId(),
                event.getTitle(),
                event.getDescription(),
                event.getCategory(),
                event.getStatus(),
                event.getImportance(),
                event.getFirstDetectedAt(),
                event.getLastUpdatedAt(),
                event.getCreatedAt(),
                event.getUpdatedAt(),
                event.isManualDiscarded(),
                event.getManualDiscardedAt()
        );
    }

    private Event toDomain(EventEntity entity) {
        return new Event(
                entity.getId(),
                entity.getTitle(),
                entity.getDescription(),
                entity.getCategory(),
                entity.getImportance(),
                entity.getStatus(),
                entity.isManualDiscarded(),
                entity.getManualDiscardedAt(),
                findNewsIdsByEventId(entity.getId()),
                entity.getFirstDetectedAt(),
                entity.getLastUpdatedAt(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }

    private void validateConfidenceScore(Integer confidenceScore) {
        if (confidenceScore != null && (confidenceScore < 0 || confidenceScore > 100)) {
            throw new IllegalArgumentException("confidenceScore must be between 0 and 100");
        }
    }
}
