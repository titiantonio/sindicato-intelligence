import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { PublicationListItem } from '../models/publication.models';

@Injectable({
  providedIn: 'root'
})
export class PublicationService {
  private readonly httpClient = inject(HttpClient);

  listPublications() {
    return this.httpClient.get<PublicationListItem[]>('/api/v1/publications');
  }

  getPublication(publicationId: number) {
    return this.httpClient.get<PublicationListItem>(`/api/v1/publications/${publicationId}`);
  }

  publishContent(contentId: number) {
    return this.httpClient.post<PublicationListItem>(`/api/v1/publications/${contentId}/publish`, {});
  }

  schedulePublication(contentId: number, scheduledAt: string) {
    return this.httpClient.post<PublicationListItem>(`/api/v1/publications/${contentId}/schedule`, { scheduledAt });
  }
}
