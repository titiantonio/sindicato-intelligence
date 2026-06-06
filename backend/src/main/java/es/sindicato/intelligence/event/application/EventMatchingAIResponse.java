package es.sindicato.intelligence.event.application;

public record EventMatchingAIResponse(
        boolean match,
        Long eventId,
        int confidence,
        String reason
) {
    public EventMatchingAIResponse {
        if (confidence < 0 || confidence > 100) {
            throw new IllegalArgumentException("confidence must be between 0 and 100");
        }
    }
}
