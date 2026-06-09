package es.sindicato.intelligence.publication.application;

public record PublishingRequest(
        Long contentId,
        String channel,
        String title,
        String message
) {
    public PublishingRequest {
        if (contentId == null) {
            throw new IllegalArgumentException("contentId is required");
        }
        if (channel == null || channel.isBlank()) {
            throw new IllegalArgumentException("channel is required");
        }
        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("title is required");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("message is required");
        }
    }
}
