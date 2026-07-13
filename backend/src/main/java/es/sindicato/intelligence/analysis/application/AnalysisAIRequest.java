package es.sindicato.intelligence.analysis.application;

import es.sindicato.intelligence.event.domain.EventCategory;
import es.sindicato.intelligence.event.domain.Importance;
import es.sindicato.intelligence.analysis.domain.AnalysisType;

import java.util.List;

public record AnalysisAIRequest(
        Long eventId,
        String eventTitle,
        String eventDescription,
        EventCategory category,
        Importance importance,
        AnalysisType analysisType,
        List<AnalysisNewsItem> news,
        String systemPrompt,
        String userPrompt
) {

    public AnalysisAIRequest(
            Long eventId,
            String eventTitle,
            String eventDescription,
            EventCategory category,
            Importance importance,
            List<AnalysisNewsItem> news,
            String systemPrompt,
            String userPrompt
    ) {
        this(eventId, eventTitle, eventDescription, category, importance, AnalysisType.STANDARD, news, systemPrompt, userPrompt);
    }
}
