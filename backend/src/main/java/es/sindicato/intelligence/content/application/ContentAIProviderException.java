package es.sindicato.intelligence.content.application;

public class ContentAIProviderException extends RuntimeException {

    public ContentAIProviderException(String message) {
        super(message);
    }

    public ContentAIProviderException(String message, Throwable cause) {
        super(message, cause);
    }
}
