import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import {
  AiMetricsSnapshot,
  AiModelOption,
  AiPromptVersion,
  AiProviderSetting,
  AiWorkflowSetting,
  UpdateAiProviderSettingRequest,
  UpdateAiWorkflowSettingRequest
} from '../models/ai-observability.models';

@Injectable({
  providedIn: 'root'
})
export class AiObservabilityService {
  private readonly httpClient = inject(HttpClient);

  listPrompts() {
    return this.httpClient.get<AiPromptVersion[]>('/api/v1/ai/prompts');
  }

  listMetrics(limit = 25) {
    return this.httpClient.get<AiMetricsSnapshot>(`/api/v1/ai/metrics?limit=${limit}`);
  }

  listDailyMetrics(date: string) {
    return this.httpClient.get<AiMetricsSnapshot>(`/api/v1/ai/metrics?date=${date}`);
  }

  listProviders() {
    return this.httpClient.get<AiProviderSetting[]>('/api/v1/ai/providers');
  }

  updateProvider(providerCode: string, payload: UpdateAiProviderSettingRequest) {
    return this.httpClient.put<AiProviderSetting>(`/api/v1/ai/providers/${providerCode}`, payload);
  }

  listProviderModels(providerCode: string, apiKey: string | null = null) {
    return this.httpClient.post<AiModelOption[]>(`/api/v1/ai/providers/${providerCode}/models`, { apiKey });
  }

  listWorkflowSettings() {
    return this.httpClient.get<AiWorkflowSetting[]>('/api/v1/ai/workflow-settings');
  }

  updateWorkflowSetting(workflowCode: string, payload: UpdateAiWorkflowSettingRequest) {
    return this.httpClient.put<AiWorkflowSetting>(`/api/v1/ai/workflow-settings/${workflowCode}`, payload);
  }
}
