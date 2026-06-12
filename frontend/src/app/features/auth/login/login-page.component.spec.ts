import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { LoginResponse } from '../../../core/models/auth.models';
import { AuthService } from '../../../core/services/auth.service';
import { LoginPageComponent } from './login-page.component';

describe('LoginPageComponent', () => {
  let fixture: ComponentFixture<LoginPageComponent>;
  let component: LoginPageComponent;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  const response: LoginResponse = {
    accessToken: 'access-token',
    refreshToken: 'refresh-token',
    user: {
      id: 1,
      name: 'Admin',
      role: 'ADMIN',
      mustChangePassword: false
    }
  };

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['login']);

    await TestBed.configureTestingModule({
      imports: [LoginPageComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    }).compileComponents();

    fixture = TestBed.createComponent(LoginPageComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);
    fixture.detectChanges();
  });

  it('does not submit invalid forms', () => {
    (component as any).loginForm.setValue({ email: 'bad-email', password: '' });

    (component as any).submit();

    expect(authService.login).not.toHaveBeenCalled();
    expect((component as any).loginForm.touched).toBeTrue();
  });

  it('logs in and navigates to dashboard', () => {
    authService.login.and.returnValue(of(response));
    (component as any).loginForm.setValue({
      email: 'admin@sindicato.es',
      password: 'Admin@12345'
    });

    (component as any).submit();

    expect(authService.login).toHaveBeenCalledWith({
      email: 'admin@sindicato.es',
      password: 'Admin@12345'
    });
    expect(router.navigate).toHaveBeenCalledWith(['/dashboard']);
    expect((component as any).isSubmitting()).toBeFalse();
  });

  it('navigates to forced password change when required', () => {
    authService.login.and.returnValue(of({
      ...response,
      user: { ...response.user, mustChangePassword: true }
    }));
    (component as any).loginForm.setValue({
      email: 'editor@sindicato.es',
      password: 'TempPass1!'
    });

    (component as any).submit();

    expect(router.navigate).toHaveBeenCalledWith(['/change-password']);
  });

  it('shows backend errors', () => {
    authService.login.and.returnValue(throwError(() => ({ error: { error: 'Credenciales invalidas' } })));
    (component as any).loginForm.setValue({
      email: 'admin@sindicato.es',
      password: 'bad'
    });

    (component as any).submit();

    expect((component as any).errorMessage()).toBe('Credenciales invalidas');
    expect((component as any).isSubmitting()).toBeFalse();
  });
});
