package es.sindicato.intelligence.classification.application;

public record ClassifyNewsPrompt(
        String systemPrompt,
        String userPrompt
) {
}
