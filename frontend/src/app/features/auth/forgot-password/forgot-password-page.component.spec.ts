import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { ForgotPasswordPageComponent } from './forgot-password-page.component';

describe('ForgotPasswordPageComponent', () => {
  let fixture: ComponentFixture<ForgotPasswordPageComponent>;
  let component: ForgotPasswordPageComponent;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['requestPasswordReset']);

    await TestBed.configureTestingModule({
      imports: [ForgotPasswordPageComponent],
      providers: [provideRouter([]), { provide: AuthService, useValue: authService }]
    }).compileComponents();

    fixture = TestBed.createComponent(ForgotPasswordPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('does not submit invalid email', () => {
    (component as any).forgotPasswordForm.setValue({ email: 'bad-email' });

    (component as any).submit();

    expect(authService.requestPasswordReset).not.toHaveBeenCalled();
    expect((component as any).forgotPasswordForm.touched).toBeTrue();
  });

  it('requests a password reset link', () => {
    authService.requestPasswordReset.and.returnValue(of({ message: 'Correo enviado' }));
    (component as any).forgotPasswordForm.setValue({ email: 'editor@sindicato.es' });

    (component as any).submit();

    expect(authService.requestPasswordReset).toHaveBeenCalledWith({ email: 'editor@sindicato.es' });
    expect((component as any).successMessage()).toBe('Correo enviado');
    expect((component as any).isSubmitting()).toBeFalse();
  });

  it('shows request errors', () => {
    authService.requestPasswordReset.and.returnValue(
      throwError(() => ({ error: { error: 'Email no encontrado' } }))
    );
    (component as any).forgotPasswordForm.setValue({ email: 'missing@sindicato.es' });

    (component as any).submit();

    expect((component as any).errorMessage()).toBe('Email no encontrado');
    expect((component as any).isSubmitting()).toBeFalse();
  });
});
