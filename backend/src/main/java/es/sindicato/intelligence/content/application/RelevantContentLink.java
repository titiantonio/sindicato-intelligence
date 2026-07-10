package es.sindicato.intelligence.content.application;

public record RelevantContentLink(
        Long newsId,
        String label,
        String url
) {
}
