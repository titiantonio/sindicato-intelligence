import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { CreateSourceRequest, SourceResponse, UpdateSourceRequest } from '../models/source.models';

@Injectable({
  providedIn: 'root'
})
export class SourceService {
  private readonly httpClient = inject(HttpClient);

  listSources() {
    return this.httpClient.get<SourceResponse[]>('/api/v1/sources');
  }

  createSource(request: CreateSourceRequest) {
    return this.httpClient.post<SourceResponse>('/api/v1/sources', request);
  }

  updateSource(sourceId: number, request: UpdateSourceRequest) {
    return this.httpClient.put<SourceResponse>(`/api/v1/sources/${sourceId}`, request);
  }
}