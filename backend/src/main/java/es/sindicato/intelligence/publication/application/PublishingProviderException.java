package es.sindicato.intelligence.publication.application;

public class PublishingProviderException extends RuntimeException {

    public PublishingProviderException(String message) {
        super(message);
    }

    public PublishingProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
