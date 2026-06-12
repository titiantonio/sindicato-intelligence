import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { MetricCard, PriorityEvent } from '../models/dashboard.models';

export interface DashboardResponse {
  metricCards: MetricCard[];
  priorityEvents: PriorityEvent[];
}

@Injectable({
  providedIn: 'root'
})
export class DashboardService {
  private readonly httpClient = inject(HttpClient);

  getDashboard() {
    return this.httpClient.get<DashboardResponse>('/api/v1/dashboard');
  }
}