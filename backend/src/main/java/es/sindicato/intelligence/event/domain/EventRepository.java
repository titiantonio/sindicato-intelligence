package es.sindicato.intelligence.event.domain;

import java.util.List;
import java.util.Optional;

public interface EventRepository {

    Event save(Event event);

    Optional<Event> findById(Long id);

    List<Event> findAll();

    List<Event> findByStatus(EventStatus status);

    List<Event> findByCategory(EventCategory category);

    List<Event> findByImportance(Importance importance);

    List<Event> findByStatusIn(List<EventStatus> statuses);

    Optional<Event> findByNewsId(Long newsId);

    default void saveNewsAssociation(Long eventId, Long newsId, Integer confidenceScore) {
        saveNewsAssociation(eventId, newsId, confidenceScore, null, null);
    }

    void saveNewsAssociation(Long eventId, Long newsId, Integer confidenceScore, EventMatchDecision matchDecision, String matchReason);

    boolean existsNewsAssociation(Long newsId);
}
