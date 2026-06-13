import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { ContentListItem } from '../models/content.models';

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

  approveContent(contentId: number) {
    return this.httpClient.post<ContentListItem>(`/api/v1/content/${contentId}/approve`, {});
  }

  rejectContent(contentId: number) {
    return this.httpClient.post<ContentListItem>(`/api/v1/content/${contentId}/reject`, {});
  }

  updateContent(contentId: number, payload: { title: string; content: string; tone: string }) {
    return this.httpClient.put<ContentListItem>(`/api/v1/content/${contentId}`, payload);
  }
}
