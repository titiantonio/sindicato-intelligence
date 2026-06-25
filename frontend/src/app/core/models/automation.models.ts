export interface AutomationRunError {
  entityId: number | null;
  message: string;
}

export interface AutomationRunResult {
  processedCount: number;
  successCount: number;
  failedCount: number;
  skippedCount: number;
  errors: AutomationRunError[];
}

export type AutomationWorkflowCode = 'WF02_CLASSIFICATION' | 'WF03_EVENT_DETECTION' | 'WF04_ANALYSIS';

export interface AutomationWorkflowSetting {
  workflowCode: AutomationWorkflowCode;
  enabled: boolean;
  intervalSeconds: number;
  batchSize: number;
  running: boolean;
  lastRunAt: string | null;
  lastSuccessAt: string | null;
  lastFailureAt: string | null;
  nextRunAt: string;
  lastProcessedCount: number;
  lastSuccessCount: number;
  lastFailedCount: number;
  lastSkippedCount: number;
  lastError: string | null;
}

export interface UpdateAutomationSettingRequest {
  enabled: boolean;
  intervalSeconds: number;
  batchSize: number;
}

export interface AutomationOverview {
  n8nWorkflowCode: string;
  n8nWorkflowName: string;
  n8nStatus: string;
  backendEnabledCount: number;
  backendFailedCount: number;
  backendRunningCount: number;
  backendWorkflows: AutomationWorkflowSetting[];
}

export interface WorkflowOperation {
  id: string;
  workflowCode: string;
  operationType: string;
  status: 'SUCCESS' | 'FAILED' | string;
  relatedEntityType: string | null;
  relatedEntityId: number | null;
  createdAt: string;
  latencyMs: number | null;
  promptKey: string | null;
  provider: string | null;
  model: string | null;
  errorMessage: string | null;
  details: Record<string, unknown>;
}
