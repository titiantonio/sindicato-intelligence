package es.sindicato.intelligence.ai.application;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiErrorSanitizerTest {

    @Test
    void replacesSensitiveMetricMessages() {
        assertEquals(
                "AI provider request failed",
                AiErrorSanitizer.metricMessage("Gemini failed with apiKey=secret and prompt payload")
        );
    }

    @Test
    void keepsOperationalMetricMessagesWithoutSensitiveValues() {
        assertEquals(
                "Gemini classification request failed with HTTP 429",
                AiErrorSanitizer.metricMessage("Gemini classification request failed with HTTP 429")
        );
    }
}
