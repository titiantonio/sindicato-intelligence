package es.sindicato.intelligence.source.application;

public record UpdateSourceCommand(
        String name,
        String url,
        String type,
        int priority,
        boolean active
) {
}
