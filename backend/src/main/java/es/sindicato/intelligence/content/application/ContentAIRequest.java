package es.sindicato.intelligence.content.application;

import es.sindicato.intelligence.analysis.domain.EventAIAnalysis;
import es.sindicato.intelligence.content.domain.ContentType;
import es.sindicato.intelligence.event.domain.Event;

import java.util.List;

public record ContentAIRequest(
        Event event,
        EventAIAnalysis analysis,
        String channel,
        String tone,
        ContentType contentType,
        String length,
        List<RelevantContentLink> relevantLinks,
        ContentGenerationContext generationContext,
        String systemPrompt,
        String userPrompt
) {
}
