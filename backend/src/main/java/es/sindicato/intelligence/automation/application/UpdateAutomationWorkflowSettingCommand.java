package es.sindicato.intelligence.automation.application;

public record UpdateAutomationWorkflowSettingCommand(
        boolean enabled,
        int intervalSeconds,
        int batchSize
) {
}
