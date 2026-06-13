import { HttpClient, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { LoginResponse } from '../models/auth.models';
import { AuthService } from '../services/auth.service';
import { jwtInterceptor } from './jwt.interceptor';

describe('jwtInterceptor', () => {
  let httpClient: HttpClient;
  let httpTestingController: HttpTestingController;

  const loginResponse: LoginResponse = {
    accessToken: 'expired-access-token',
    refreshToken: 'refresh-token',
    user: {
      id: 1,
      name: 'Admin',
      role: 'ADMIN',
      mustChangePassword: false
    }
  };

  beforeEach(() => {
    localStorage.clear();
    const router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([jwtInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: router }
      ]
    });

    httpClient = TestBed.inject(HttpClient);
    httpTestingController = TestBed.inject(HttpTestingController);
    const authService = TestBed.inject(AuthService);

    authService.login({ email: 'admin@sindicato.es', password: 'Admin@12345' }).subscribe();
    httpTestingController.expectOne('/api/v1/auth/login').flush(loginResponse);
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('refreshes the session and retries a request after unauthorized response', () => {
    let response: { ok: boolean } | undefined;

    httpClient.get<{ ok: boolean }>('/api/v1/dashboard').subscribe((value) => {
      response = value;
    });

    const firstDashboardRequest = httpTestingController.expectOne('/api/v1/dashboard');
    expect(firstDashboardRequest.request.headers.get('Authorization')).toBe('Bearer expired-access-token');
    firstDashboardRequest.flush({ error: 'Unauthorized' }, { status: 401, statusText: 'Unauthorized' });

    const refreshRequest = httpTestingController.expectOne('/api/v1/auth/refresh');
    expect(refreshRequest.request.headers.has('Authorization')).toBeFalse();
    expect(refreshRequest.request.body).toEqual({ refreshToken: 'refresh-token' });
    refreshRequest.flush({
      ...loginResponse,
      accessToken: 'new-access-token',
      refreshToken: 'new-refresh-token'
    });

    const retriedDashboardRequest = httpTestingController.expectOne('/api/v1/dashboard');
    expect(retriedDashboardRequest.request.headers.get('Authorization')).toBe('Bearer new-access-token');
    retriedDashboardRequest.flush({ ok: true });

    expect(response).toEqual({ ok: true });
  });

  it('does not refresh when backend rejects business credentials', () => {
    let errorStatus: number | undefined;

    httpClient.post('/api/v1/auth/change-password', {
      currentPassword: 'wrong',
      newPassword: 'ValidPass1!'
    }).subscribe({
      error: (error) => {
        errorStatus = error.status;
      }
    });

    const request = httpTestingController.expectOne('/api/v1/auth/change-password');
    expect(request.request.headers.get('Authorization')).toBe('Bearer expired-access-token');
    request.flush({ error: 'invalid credentials' }, { status: 401, statusText: 'Unauthorized' });

    httpTestingController.expectNone('/api/v1/auth/refresh');
    expect(errorStatus).toBe(401);
  });
});
