import { HttpClient } from '@angular/common/http';
import { computed, inject, Injectable, signal } from '@angular/core';
import { Router } from '@angular/router';
import { tap } from 'rxjs';

import {
  AuthSession,
  ChangePasswordRequest,
  ForgotPasswordRequest,
  LoginRequest,
  LoginResponse,
  MessageResponse,
  RequestTemporaryPasswordRequest,
  ResetPasswordRequest,
  UserRole
} from '../models/auth.models';
import { StorageService } from './storage.service';

const SESSION_STORAGE_KEY = 'sindicato-intelligence.session';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly httpClient = inject(HttpClient);
  private readonly router = inject(Router);
  private readonly storageService = inject(StorageService);

  private readonly sessionState = signal<AuthSession | null>(this.storageService.getItem<AuthSession>(SESSION_STORAGE_KEY));

  readonly session = this.sessionState.asReadonly();
  readonly isAuthenticated = computed(() => this.sessionState() !== null);
  readonly currentUser = computed(() => this.sessionState()?.user ?? null);
  readonly currentRole = computed(() => this.sessionState()?.user.role ?? null);
  readonly accessToken = computed(() => this.sessionState()?.accessToken ?? null);

  login(request: LoginRequest) {
    return this.httpClient.post<LoginResponse>('/api/v1/auth/login', request).pipe(
      tap((response) => {
        const session: AuthSession = {
          accessToken: response.accessToken,
          refreshToken: response.refreshToken,
          user: response.user
        };

        this.sessionState.set(session);
        this.storageService.setItem(SESSION_STORAGE_KEY, session);
      })
    );
  }

  requestPasswordReset(request: ForgotPasswordRequest) {
    return this.httpClient.post<MessageResponse>('/api/v1/auth/forgot-password', request);
  }

  resetPassword(request: ResetPasswordRequest) {
    return this.httpClient.post<MessageResponse>('/api/v1/auth/reset-password', request);
  }

  requestTemporaryPassword(request: RequestTemporaryPasswordRequest) {
    return this.httpClient.post<MessageResponse>('/api/v1/auth/request-temporary-password', request);
  }

  changePassword(request: ChangePasswordRequest) {
    return this.httpClient.post<MessageResponse>('/api/v1/auth/change-password', request);
  }

  logout(): void {
    this.sessionState.set(null);
    this.storageService.removeItem(SESSION_STORAGE_KEY);
    void this.router.navigate(['/login']);
  }

  hasRole(roles: UserRole[]): boolean {
    const role = this.currentRole();
    return role !== null && roles.includes(role);
  }
}
