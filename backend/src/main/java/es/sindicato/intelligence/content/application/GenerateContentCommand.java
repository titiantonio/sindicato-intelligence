package es.sindicato.intelligence.content.application;

public record GenerateContentCommand(
        Long eventId,
        Long analysisId,
        String channel,
        String tone,
        String contentType,
        String length
) {
}
