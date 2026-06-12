import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { ActivatedRoute, Router, convertToParamMap, provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { ResetPasswordPageComponent } from './reset-password-page.component';

describe('ResetPasswordPageComponent', () => {
  let fixture: ComponentFixture<ResetPasswordPageComponent>;
  let component: ResetPasswordPageComponent;
  let authService: jasmine.SpyObj<AuthService>;
  let router: Router;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['resetPassword']);

    await TestBed.configureTestingModule({
      imports: [ResetPasswordPageComponent],
      providers: [
        provideRouter([]),
        { provide: AuthService, useValue: authService },
        {
          provide: ActivatedRoute,
          useValue: {
            snapshot: {
              queryParamMap: convertToParamMap({ token: 'query-token' })
            }
          }
        }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(ResetPasswordPageComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    spyOn(router, 'navigate').and.resolveTo(true);
    fixture.detectChanges();
  });

  it('initializes token from query params', () => {
    expect((component as any).resetPasswordForm.controls.token.value).toBe('query-token');
  });

  it('does not submit invalid password pattern', () => {
    (component as any).resetPasswordForm.setValue({
      token: 'query-token',
      newPassword: 'weak',
      confirmPassword: 'weak'
    });

    (component as any).submit();

    expect(authService.resetPassword).not.toHaveBeenCalled();
  });

  it('rejects password confirmation mismatch', () => {
    (component as any).resetPasswordForm.setValue({
      token: 'query-token',
      newPassword: 'ValidPass1!',
      confirmPassword: 'OtherPass1!'
    });

    (component as any).submit();

    expect(authService.resetPassword).not.toHaveBeenCalled();
    expect((component as any).errorMessage()).toBe('La confirmacion no coincide con la nueva password.');
  });

  it('resets password and redirects to login', fakeAsync(() => {
    authService.resetPassword.and.returnValue(of({ message: 'Password actualizada' }));
    (component as any).resetPasswordForm.setValue({
      token: 'query-token',
      newPassword: 'ValidPass1!',
      confirmPassword: 'ValidPass1!'
    });

    (component as any).submit();
    tick(1500);

    expect(authService.resetPassword).toHaveBeenCalledWith({
      token: 'query-token',
      newPassword: 'ValidPass1!'
    });
    expect((component as any).successMessage()).toBe('Password actualizada');
    expect(router.navigate).toHaveBeenCalledWith(['/login']);
  }));

  it('shows backend errors', () => {
    authService.resetPassword.and.returnValue(
      throwError(() => ({ error: { error: 'Token expirado' } }))
    );
    (component as any).resetPasswordForm.setValue({
      token: 'query-token',
      newPassword: 'ValidPass1!',
      confirmPassword: 'ValidPass1!'
    });

    (component as any).submit();

    expect((component as any).errorMessage()).toBe('Token expirado');
    expect((component as any).isSubmitting()).toBeFalse();
  });
});
