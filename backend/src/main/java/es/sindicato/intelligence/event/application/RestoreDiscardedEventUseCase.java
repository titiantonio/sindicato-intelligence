package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Objects;

@Service
public class RestoreDiscardedEventUseCase {

    private final EventRepository eventRepository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public RestoreDiscardedEventUseCase(EventRepository eventRepository, RecordAuditLogUseCase recordAuditLogUseCase) {
        this.eventRepository = eventRepository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public Event execute(Long eventId) {
        Objects.requireNonNull(eventId, "eventId is required");

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new EventNotFoundException(eventId));

        event.restoreManualDiscard(OffsetDateTime.now());
        Event savedEvent = eventRepository.save(event);

        recordAuditLogUseCase.record(
                "EVENT_RESTORED",
                "EVENT",
                savedEvent.getId(),
                "Descarte manual de evento deshecho.",
                AuditDetailFormatter.eventRestored(
                        savedEvent.getId(),
                        savedEvent.getTitle(),
                        savedEvent.getImportance(),
                        savedEvent.getNewsIds().size()
                )
        );

        return savedEvent;
    }
}
