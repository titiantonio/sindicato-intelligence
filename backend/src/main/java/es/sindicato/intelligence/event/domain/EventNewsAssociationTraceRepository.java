package es.sindicato.intelligence.event.domain;

import java.util.List;

public interface EventNewsAssociationTraceRepository {

    List<EventNewsAssociationTrace> findByEventId(Long eventId);
}
