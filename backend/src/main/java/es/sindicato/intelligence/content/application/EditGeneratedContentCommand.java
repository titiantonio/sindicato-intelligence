package es.sindicato.intelligence.content.application;

public record EditGeneratedContentCommand(
        Long id,
        String title,
        String content,
        String tone
) {
}
