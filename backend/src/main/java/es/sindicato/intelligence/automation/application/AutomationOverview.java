package es.sindicato.intelligence.automation.application;

import es.sindicato.intelligence.automation.domain.AutomationWorkflowSetting;

import java.util.List;

public record AutomationOverview(
        String n8nWorkflowCode,
        String n8nWorkflowName,
        String n8nStatus,
        List<AutomationWorkflowSetting> backendWorkflows,
        long backendEnabledCount,
        long backendFailedCount,
        long backendRunningCount
) {
}
