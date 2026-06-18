import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { AiMetricsSnapshot, AiPromptVersion } from '../models/ai-observability.models';

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
}
