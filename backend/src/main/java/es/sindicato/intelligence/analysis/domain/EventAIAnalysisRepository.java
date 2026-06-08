package es.sindicato.intelligence.analysis.domain;

import java.util.List;
import java.util.Optional;

public interface EventAIAnalysisRepository {

    EventAIAnalysis save(EventAIAnalysis analysis);

    Optional<EventAIAnalysis> findById(Long id);

    List<EventAIAnalysis> findByEventId(Long eventId);
}
