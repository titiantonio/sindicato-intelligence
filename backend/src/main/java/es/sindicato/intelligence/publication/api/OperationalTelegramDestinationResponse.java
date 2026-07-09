package es.sindicato.intelligence.publication.api;

public record OperationalTelegramDestinationResponse(
        Long id,
        String name,
        boolean defaultSelected
) {
}
