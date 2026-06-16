package es.sindicato.intelligence.automation.application;

public record AutomationRunError(
        Long entityId,
        String message
) {
}
