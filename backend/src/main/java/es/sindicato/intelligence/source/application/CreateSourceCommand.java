package es.sindicato.intelligence.source.application;

public record CreateSourceCommand(
        String name,
        String url,
        String type,
        int priority,
        boolean active
) {
}
