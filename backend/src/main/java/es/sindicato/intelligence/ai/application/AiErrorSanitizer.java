package es.sindicato.intelligence.ai.application;

import org.springframework.web.client.RestClientResponseException;

public final class AiErrorSanitizer {

    private AiErrorSanitizer() {
    }

    public static String providerHttpError(String operation, RestClientResponseException exception) {
        return "Gemini " + operation + " request failed with HTTP " + exception.getStatusCode().value();
    }

    public static String publicMessage() {
        return "AI provider request failed";
    }

    public static String metricMessage(String message) {
        if (message == null || message.isBlank()) {
            return publicMessage();
        }

        String normalized = message.replaceAll("\\s+", " ").trim();
        return containsSensitiveToken(normalized) ? publicMessage() : normalized;
    }

    private static boolean containsSensitiveToken(String value) {
        String lower = value.toLowerCase();
        return lower.contains("apikey")
                || lower.contains("api_key")
                || lower.contains("api key")
                || lower.contains("authorization")
                || lower.contains("bearer ")
                || lower.contains("token=")
                || lower.contains("password")
                || lower.contains("prompt");
    }
}
