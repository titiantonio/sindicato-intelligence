package es.sindicato.intelligence.automation.api;

import java.util.List;

public record AutomationOverviewResponse(
        String n8nWorkflowCode,
        String n8nWorkflowName,
        String n8nStatus,
        long backendEnabledCount,
        long backendFailedCount,
        long backendRunningCount,
        List<AutomationWorkflowSettingResponse> backendWorkflows
) {
}
