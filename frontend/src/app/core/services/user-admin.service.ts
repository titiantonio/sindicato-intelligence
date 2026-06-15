import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import {
  CreateUserAdminRequest,
  UpdateUserAdminRequest,
  UserAdminResponse
} from '../models/user-admin.models';

@Injectable({
  providedIn: 'root'
})
export class UserAdminService {
  private readonly httpClient = inject(HttpClient);

  listUsers() {
    return this.httpClient.get<UserAdminResponse[]>('/api/v1/users');
  }

  createUser(request: CreateUserAdminRequest) {
    return this.httpClient.post<UserAdminResponse>('/api/v1/users', request);
  }

  updateUser(userId: number, request: UpdateUserAdminRequest) {
    return this.httpClient.put<UserAdminResponse>(`/api/v1/users/${userId}`, request);
  }

  activateUser(userId: number) {
    return this.httpClient.post<UserAdminResponse>(`/api/v1/users/${userId}/activate`, {});
  }

  disableUser(userId: number) {
    return this.httpClient.post<UserAdminResponse>(`/api/v1/users/${userId}/disable`, {});
  }

  lockUser(userId: number) {
    return this.httpClient.post<UserAdminResponse>(`/api/v1/users/${userId}/lock`, {});
  }

  unlockUser(userId: number) {
    return this.httpClient.post<UserAdminResponse>(`/api/v1/users/${userId}/unlock`, {});
  }

  resetTemporaryPassword(userId: number) {
    return this.httpClient.post<UserAdminResponse>(`/api/v1/users/${userId}/reset-temporary-password`, {});
  }

  deleteUser(userId: number) {
    return this.httpClient.delete<void>(`/api/v1/users/${userId}`);
  }
}
