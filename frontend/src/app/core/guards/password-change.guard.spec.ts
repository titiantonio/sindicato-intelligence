import { TestBed } from '@angular/core/testing';
import { Router, UrlTree, provideRouter } from '@angular/router';

import { AuthService } from '../services/auth.service';
import { passwordChangeGuard } from './password-change.guard';

describe('passwordChangeGuard', () => {
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(() => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['currentUser']);

    TestBed.configureTestingModule({
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    });

    router = TestBed.inject(Router);
  });

  it('redirects users that must change password', () => {
    authService.currentUser.and.returnValue({
      id: 1,
      name: 'Editor',
      role: 'EDITOR',
      mustChangePassword: true
    });

    const result = TestBed.runInInjectionContext(() => passwordChangeGuard({} as never, {} as never));

    expect(router.serializeUrl(result as UrlTree)).toBe('/change-password');
  });

  it('allows users with an already changed password', () => {
    authService.currentUser.and.returnValue({
      id: 1,
      name: 'Editor',
      role: 'EDITOR',
      mustChangePassword: false
    });

    const result = TestBed.runInInjectionContext(() => passwordChangeGuard({} as never, {} as never));

    expect(result).toBeTrue();
  });
});
