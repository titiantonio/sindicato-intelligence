import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { ManualPublicationPayload, PublicationDetail, PublicationListItem } from '../models/publication.models';

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

  getPublicationDetail(publicationId: number) {
    return this.httpClient.get<PublicationDetail>(`/api/v1/publications/${publicationId}/detail`);
  }

  publishContent(contentId: number) {
    return this.httpClient.post<PublicationListItem>(`/api/v1/publications/${contentId}/publish`, {});
  }

  schedulePublication(contentId: number, scheduledAt: string) {
    return this.httpClient.post<PublicationListItem>(`/api/v1/publications/${contentId}/schedule`, { scheduledAt });
  }

  publishManual(payload: ManualPublicationPayload) {
    const formData = new FormData();
    formData.append('channel', payload.channel);
    formData.append('title', payload.title);
    formData.append('message', payload.message);
    payload.destinationIds.forEach((destinationId) => formData.append('destinationIds', destinationId.toString()));
    payload.files.forEach((file) => formData.append('files', file, file.name));
    return this.httpClient.post<PublicationListItem>('/api/v1/publications/manual', formData);
  }
}
