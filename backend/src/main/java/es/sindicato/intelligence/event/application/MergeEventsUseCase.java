package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.audit.application.AuditDetailFormatter;
import es.sindicato.intelligence.audit.application.RecordAuditLogUseCase;
import es.sindicato.intelligence.event.domain.Event;
import es.sindicato.intelligence.event.domain.EventRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

@Service
public class MergeEventsUseCase {

    private final EventRepository eventRepository;
    private final RecordAuditLogUseCase recordAuditLogUseCase;

    public MergeEventsUseCase(EventRepository eventRepository, RecordAuditLogUseCase recordAuditLogUseCase) {
        this.eventRepository = eventRepository;
        this.recordAuditLogUseCase = recordAuditLogUseCase;
    }

    @Transactional
    public Event execute(MergeEventsCommand command) {
        Objects.requireNonNull(command.targetEventId(), "targetEventId is required");
        if (command.sourceEventIds() == null || command.sourceEventIds().isEmpty()) {
            throw new IllegalArgumentException("sourceEventIds is required");
        }

        Set<Long> sourceIds = new LinkedHashSet<>(command.sourceEventIds());
        if (sourceIds.contains(command.targetEventId())) {
            throw new IllegalArgumentException("sourceEventIds cannot contain targetEventId");
        }
        if (sourceIds.size() != command.sourceEventIds().size()) {
            throw new IllegalArgumentException("sourceEventIds cannot contain duplicates");
        }

        Event target = eventRepository.findById(command.targetEventId())
                .orElseThrow(() -> new EventNotFoundException(command.targetEventId()));
        if (!target.isActive()) {
            throw new IllegalStateException("target event must be active");
        }

        List<Event> sources = sourceIds.stream()
                .map(sourceId -> eventRepository.findById(sourceId)
                        .orElseThrow(() -> new EventNotFoundException(sourceId)))
                .toList();

        OffsetDateTime now = OffsetDateTime.now();
        Set<Long> mergedNewsIds = new LinkedHashSet<>(target.getNewsIds());
        sources.forEach(source -> mergedNewsIds.addAll(source.getNewsIds()));

        Event savedTarget = eventRepository.save(target.withNewsIds(mergedNewsIds, now));
        sources.forEach(source -> eventRepository.save(source.archivedWithoutNews(now)));

        recordAuditLogUseCase.record(
                "EVENT_MERGED",
                "EVENT",
                savedTarget.getId(),
                "Eventos origen archivados: " + sourceIds + ".",
                AuditDetailFormatter.eventMerged(savedTarget.getId(), sourceIds, savedTarget.getNewsIds().size())
        );

        return savedTarget;
    }
}
