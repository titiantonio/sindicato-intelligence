package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
                .toList();
    }
}
