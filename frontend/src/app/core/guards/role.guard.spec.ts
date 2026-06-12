import { TestBed } from '@angular/core/testing';
import { ActivatedRouteSnapshot, Router, UrlTree, provideRouter } from '@angular/router';

import { AuthService } from '../services/auth.service';
import { roleGuard } from './role.guard';

describe('roleGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['hasRole']);

    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    });

    router = TestBed.inject(Router);
  });

  it('allows routes without role metadata', () => {
    const route = { data: {} } as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as never));

    expect(result).toBeTrue();
    expect(authService.hasRole).not.toHaveBeenCalled();
  });

  it('allows users with an accepted role', () => {
    authService.hasRole.and.returnValue(true);
    const route = { data: { roles: ['ADMIN'] } } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as never));

    expect(result).toBeTrue();
    expect(authService.hasRole).toHaveBeenCalledWith(['ADMIN']);
  });

  it('redirects users without an accepted role', () => {
    authService.hasRole.and.returnValue(false);
    const route = { data: { roles: ['ADMIN'] } } as unknown as ActivatedRouteSnapshot;

    const result = TestBed.runInInjectionContext(() => roleGuard(route, {} as never));

    expect(router.serializeUrl(result as UrlTree)).toBe('/dashboard');
  });
});
