package es.sindicato.intelligence.event.application;

import java.util.List;

public record EventMatchingAIRequest(
        String newsTitle,
        String newsSummary,
        String newsContent,
        List<EventMatchCandidate> candidates,
        String systemPrompt,
        String userPrompt
) {
    public EventMatchingAIRequest {
        candidates = List.copyOf(candidates == null ? List.of() : candidates);
    }
}
