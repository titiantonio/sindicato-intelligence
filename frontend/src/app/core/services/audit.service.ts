import { HttpClient, HttpParams } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import { EditorialAuditLogItem, UserAuditLogItem } from '../models/audit.models';

@Injectable({
  providedIn: 'root'
})
export class AuditService {
  private readonly httpClient = inject(HttpClient);

  listUserAudit(limit = 100) {
    return this.httpClient.get<UserAuditLogItem[]>('/api/v1/audit/users', {
      params: new HttpParams().set('limit', limit)
    });
  }

  listEditorialAudit(limit = 100) {
    return this.httpClient.get<EditorialAuditLogItem[]>('/api/v1/audit/editorial', {
      params: new HttpParams().set('limit', limit)
    });
  }
}
