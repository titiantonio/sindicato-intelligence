package es.sindicato.intelligence.event.application;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysisRepository;
import es.sindicato.intelligence.content.domain.ContentStatus;
import es.sindicato.intelligence.content.domain.GeneratedContentRepository;
import es.sindicato.intelligence.event.domain.Event;
import org.springframework.stereotype.Component;

@Component
public class EventEditorialStatusResolver {

    private final EventAIAnalysisRepository analysisRepository;
    private final GeneratedContentRepository contentRepository;

    public EventEditorialStatusResolver(
            EventAIAnalysisRepository analysisRepository,
            GeneratedContentRepository contentRepository
    ) {
        this.analysisRepository = analysisRepository;
        this.contentRepository = contentRepository;
    }

    public EventEditorialStatus resolve(Event event) {
        if (event.isManualDiscarded()) {
            return EventEditorialStatus.DISCARDED;
        }

        boolean hasPublishedContent = contentRepository.findByEventId(event.getId()).stream()
                .anyMatch(content -> content.getStatus() == ContentStatus.PUBLISHED);
        if (hasPublishedContent) {
            return EventEditorialStatus.PUBLISHED;
        }

        var latestAnalysis = analysisRepository.findLatestByEventId(event.getId());
        if (latestAnalysis.isPresent() && latestAnalysis.get().isOutdatedFor(event.getUpdatedAt())) {
            return EventEditorialStatus.ANALYSIS_OUTDATED;
        }

        if (latestAnalysis.isPresent()) {
            return EventEditorialStatus.ANALYZED;
        }

        return EventEditorialStatus.PENDING_ANALYSIS;
    }
}
