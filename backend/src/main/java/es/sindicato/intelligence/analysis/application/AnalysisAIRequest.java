package es.sindicato.intelligence.analysis.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.Importance;

import java.util.List;

public record AnalysisAIRequest(
        Long eventId,
        String eventTitle,
        String eventDescription,
        EventCategory category,
        Importance importance,
        List<AnalysisNewsItem> news,
        String systemPrompt,
        String userPrompt
) {
}
