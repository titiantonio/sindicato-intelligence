package es.sindicato.intelligence.classification.application;

public record ClassificationAIRequest(
        String title,
        String url,
        String summary,
        String content,
        String systemPrompt,
        String userPrompt
) {
}
