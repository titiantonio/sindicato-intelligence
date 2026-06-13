import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';

import { LoginResponse } from '../models/auth.models';
import { AuthService } from './auth.service';

describe('AuthService', () => {
  let service: AuthService;
  let httpTestingController: HttpTestingController;
  let router: jasmine.SpyObj<Router>;

  const loginResponse: LoginResponse = {
    accessToken: 'access-token',
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
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(),
        provideHttpClientTesting(),
        { provide: Router, useValue: router }
      ]
    });

    service = TestBed.inject(AuthService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
    localStorage.clear();
  });

  it('logs in, stores the session and exposes role helpers', () => {
    service.login({ email: 'admin@sindicato.es', password: 'Admin@12345' }).subscribe();

    const request = httpTestingController.expectOne('/api/v1/auth/login');
    expect(request.request.method).toBe('POST');
    request.flush(loginResponse);

    expect(service.accessToken()).toBe('access-token');
    expect(service.currentUser()?.name).toBe('Admin');
    expect(service.hasRole(['ADMIN'])).toBeTrue();
    expect(service.hasRole(['EDITOR'])).toBeFalse();
    expect(localStorage.getItem('sindicato-intelligence.session')).toContain('access-token');
  });

  it('requests password recovery', () => {
    service.requestPasswordReset({ email: 'editor@sindicato.es' }).subscribe();

    const request = httpTestingController.expectOne('/api/v1/auth/forgot-password');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ email: 'editor@sindicato.es' });
    request.flush({ message: 'ok' });
  });

  it('resets password with a token', () => {
    service.resetPassword({ token: 'token', newPassword: 'ValidPass1!' }).subscribe();

    const request = httpTestingController.expectOne('/api/v1/auth/reset-password');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ token: 'token', newPassword: 'ValidPass1!' });
    request.flush({ message: 'ok' });
  });

  it('requests a new temporary password', () => {
    service.requestTemporaryPassword({ email: 'editor@sindicato.es' }).subscribe();

    const request = httpTestingController.expectOne('/api/v1/auth/request-temporary-password');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ email: 'editor@sindicato.es' });
    request.flush({ message: 'ok' });
  });

  it('refreshes the stored session', () => {
    service.login({ email: 'admin@sindicato.es', password: 'Admin@12345' }).subscribe();
    httpTestingController.expectOne('/api/v1/auth/login').flush(loginResponse);

    service.refreshSession().subscribe();

    const request = httpTestingController.expectOne('/api/v1/auth/refresh');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ refreshToken: 'refresh-token' });
    request.flush({
      ...loginResponse,
      accessToken: 'new-access-token',
      refreshToken: 'new-refresh-token'
    });

    expect(service.accessToken()).toBe('new-access-token');
    expect(service.refreshToken()).toBe('new-refresh-token');
    expect(localStorage.getItem('sindicato-intelligence.session')).toContain('new-access-token');
  });

  it('changes password', () => {
    service.changePassword({ currentPassword: 'TempPass1!', newPassword: 'ValidPass1!' }).subscribe();

    const request = httpTestingController.expectOne('/api/v1/auth/change-password');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      currentPassword: 'TempPass1!',
      newPassword: 'ValidPass1!'
    });
    request.flush({ message: 'ok' });
  });

  it('clears the session on logout', () => {
    service.login({ email: 'admin@sindicato.es', password: 'Admin@12345' }).subscribe();
    httpTestingController.expectOne('/api/v1/auth/login').flush(loginResponse);

    service.logout();

    expect(service.isAuthenticated()).toBeFalse();
    expect(localStorage.getItem('sindicato-intelligence.session')).toBeNull();
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  });
});
