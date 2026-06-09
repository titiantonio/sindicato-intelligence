package es.sindicato.intelligence.publication.application;

public record PublishingResult(
        String externalId,
        String responsePayload
) {
    public PublishingResult {
        if (externalId == null || externalId.isBlank()) {
            throw new IllegalArgumentException("externalId is required");
        }
    }
}
