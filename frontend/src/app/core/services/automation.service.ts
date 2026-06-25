import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import {
  AutomationOverview,
  AutomationRunResult,
  AutomationWorkflowCode,
  AutomationWorkflowSetting,
  UpdateAutomationSettingRequest,
  WorkflowOperation
} from '../models/automation.models';

@Injectable({
  providedIn: 'root'
})
export class AutomationService {
  private readonly httpClient = inject(HttpClient);

  runClassifications() {
    return this.httpClient.post<AutomationRunResult>('/api/v1/automation/classifications/run', {});
  }

  runEventDetection() {
    return this.httpClient.post<AutomationRunResult>('/api/v1/automation/events/run', {});
  }

  runAnalysis(eventId?: number) {
    const payload = eventId === undefined ? {} : { eventId };
    return this.httpClient.post<AutomationRunResult>('/api/v1/automation/analysis/run', payload);
  }

  listSettings() {
    return this.httpClient.get<AutomationWorkflowSetting[]>('/api/v1/automation/settings');
  }

  getSetting(workflowCode: AutomationWorkflowCode) {
    return this.httpClient.get<AutomationWorkflowSetting>(`/api/v1/automation/settings/${workflowCode}`);
  }

  updateSetting(workflowCode: AutomationWorkflowCode, payload: UpdateAutomationSettingRequest) {
    return this.httpClient.put<AutomationWorkflowSetting>(`/api/v1/automation/settings/${workflowCode}`, payload);
  }

  runWorkflow(workflowCode: AutomationWorkflowCode) {
    return this.httpClient.post<AutomationRunResult>(`/api/v1/automation/settings/${workflowCode}/run`, {});
  }

  getOverview() {
    return this.httpClient.get<AutomationOverview>('/api/v1/automation/overview');
  }

  listOperations(date: string) {
    return this.httpClient.get<WorkflowOperation[]>(`/api/v1/automation/operations?date=${date}`);
  }
}
