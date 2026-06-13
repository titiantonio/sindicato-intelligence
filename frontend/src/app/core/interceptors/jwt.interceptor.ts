import { HttpInterceptorFn, HttpRequest } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, switchMap, throwError } from 'rxjs';

import { AuthService } from '../services/auth.service';

export const jwtInterceptor: HttpInterceptorFn = (request, next) => {
  const authService = inject(AuthService);
  const accessToken = authService.accessToken();

  if (!accessToken || isAuthRequest(request.url)) {
    return next(request);
  }

  const authenticatedRequest = withAuthorization(request, accessToken);

  return next(authenticatedRequest).pipe(
    catchError((error) => {
      if (error.status !== 401 || isBusinessUnauthorized(error) || !authService.refreshToken()) {
        return throwError(() => error);
      }

      return authService.refreshSession().pipe(
        switchMap(() => {
          const refreshedAccessToken = authService.accessToken();
          if (!refreshedAccessToken) {
            return throwError(() => error);
          }

          return next(withAuthorization(request, refreshedAccessToken));
        }),
        catchError((refreshError) => {
          authService.logout();
          return throwError(() => refreshError);
        })
      );
    })
  );
};

function isAuthRequest(url: string): boolean {
  return url.includes('/api/v1/auth/login') || url.includes('/api/v1/auth/refresh');
}

function isBusinessUnauthorized(error: unknown): boolean {
  if (typeof error !== 'object' || error === null || !('error' in error)) {
    return false;
  }

  const responseBody = (error as { error?: unknown }).error;
  if (typeof responseBody !== 'object' || responseBody === null || !('error' in responseBody)) {
    return false;
  }

  return (responseBody as { error?: unknown }).error === 'invalid credentials';
}

function withAuthorization(request: HttpRequest<unknown>, accessToken: string): HttpRequest<unknown> {
  return request.clone({
    setHeaders: {
      Authorization: `Bearer ${accessToken}`
    }
  });
}
