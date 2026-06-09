package es.sindicato.intelligence.content.application;

import java.util.List;

public record ContentAIResponse(
        String title,
        String message,
        List<String> hashtags
) {
}
