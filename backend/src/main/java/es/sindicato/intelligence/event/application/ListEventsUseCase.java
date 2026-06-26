package es.sindicato.intelligence.event.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ListEventsUseCase {

    private final EventSummaryQueryRepository eventSummaryQueryRepository;

    public ListEventsUseCase(EventSummaryQueryRepository eventSummaryQueryRepository) {
        this.eventSummaryQueryRepository = eventSummaryQueryRepository;
    }

    @Transactional(readOnly = true)
    public List<EventSummaryView> execute() {
        return eventSummaryQueryRepository.findVisibleSummaries();
    }
}
