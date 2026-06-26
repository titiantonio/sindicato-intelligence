import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { EventAnalysisItem } from '../models/event.models';

@Injectable({
  providedIn: 'root'
})
export class AnalysisService {
  private readonly httpClient = inject(HttpClient);

  generateAnalysis(eventId: number) {
    return this.httpClient.post<EventAnalysisItem>('/api/v1/analysis/generate', { eventId });
  }
}
