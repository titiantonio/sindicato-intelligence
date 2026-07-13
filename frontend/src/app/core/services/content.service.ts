import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { ContentDetail, ContentListItem } from '../models/content.models';

export interface GenerateContentPayload {
  eventId: number;
  analysisId: number | null;
  channel: string;
  tone: string;
  contentType: string;
  length: string;
}

@Injectable({
  providedIn: 'root'
})
export class ContentService {
  private readonly httpClient = inject(HttpClient);

  listContent() {
    return this.httpClient.get<ContentListItem[]>('/api/v1/content');
  }

  getContent(contentId: number) {
    return this.httpClient.get<ContentListItem>(`/api/v1/content/${contentId}`);
  }

  getContentDetail(contentId: number) {
    return this.httpClient.get<ContentDetail>(`/api/v1/content/${contentId}/detail`);
  }

  approveContent(contentId: number) {
    return this.httpClient.post<ContentListItem>(`/api/v1/content/${contentId}/approve`, {});
  }

  rejectContent(contentId: number) {
    return this.httpClient.post<ContentListItem>(`/api/v1/content/${contentId}/reject`, {});
  }

  updateContent(contentId: number, payload: { title: string; content: string; tone: string }) {
    return this.httpClient.put<ContentListItem>(`/api/v1/content/${contentId}`, payload);
  }

  generateContent(payload: GenerateContentPayload) {
    return this.httpClient.post<ContentListItem>('/api/v1/content/generate', payload);
  }
}
