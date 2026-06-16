package es.sindicato.intelligence.automation.api;

public record AutomationRunErrorResponse(
        Long entityId,
        String message
) {
}
