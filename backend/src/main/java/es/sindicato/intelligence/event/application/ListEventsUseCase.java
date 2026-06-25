package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import es.sindicato.intelligence.event.domain.Importance;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;

@Service
public class ListEventsUseCase {

    private final EventRepository eventRepository;
    private final EventVisibilityPolicy visibilityPolicy;

    public ListEventsUseCase(
            EventRepository eventRepository,
            EventVisibilityPolicy visibilityPolicy
    ) {
        this.eventRepository = eventRepository;
        this.visibilityPolicy = visibilityPolicy;
    }

    @Transactional(readOnly = true)
    public List<Event> execute() {
        return eventRepository.findAll().stream()
                .filter(visibilityPolicy::isVisible)
                .sorted(editorialPriority())
                .toList();
    }

    private Comparator<Event> editorialPriority() {
        return Comparator
                .comparingInt((Event event) -> importanceRank(event.getImportance()))
                .thenComparing(Comparator.comparingInt((Event event) -> event.getNewsIds().size()).reversed())
                .thenComparing(Event::getLastUpdatedAt, Comparator.reverseOrder())
                .thenComparing(Event::getId, Comparator.reverseOrder());
    }

    private int importanceRank(Importance importance) {
        return switch (importance) {
            case CRITICAL -> 0;
            case HIGH -> 1;
            case MEDIUM -> 2;
            case LOW -> 3;
        };
    }
}
