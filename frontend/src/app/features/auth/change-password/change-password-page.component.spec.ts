import { ComponentFixture, TestBed, fakeAsync, tick } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { AuthService } from '../../../core/services/auth.service';
import { ChangePasswordPageComponent } from './change-password-page.component';

describe('ChangePasswordPageComponent', () => {
  let fixture: ComponentFixture<ChangePasswordPageComponent>;
  let component: ChangePasswordPageComponent;
  let authService: jasmine.SpyObj<AuthService>;

  beforeEach(async () => {
    authService = jasmine.createSpyObj<AuthService>('AuthService', ['changePassword', 'logout']);

    await TestBed.configureTestingModule({
      imports: [ChangePasswordPageComponent],
      providers: [{ provide: AuthService, useValue: authService }]
    }).compileComponents();

    fixture = TestBed.createComponent(ChangePasswordPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('does not submit invalid forms', () => {
    (component as any).changePasswordForm.setValue({
      currentPassword: '',
      newPassword: 'weak',
      confirmPassword: 'weak'
    });

    (component as any).submit();

    expect(authService.changePassword).not.toHaveBeenCalled();
    expect((component as any).changePasswordForm.touched).toBeTrue();
  });

  it('rejects password confirmation mismatch', () => {
    (component as any).changePasswordForm.setValue({
      currentPassword: 'TempPass1!',
      newPassword: 'ValidPass1!',
      confirmPassword: 'OtherPass1!'
    });

    (component as any).submit();

    expect(authService.changePassword).not.toHaveBeenCalled();
    expect((component as any).errorMessage()).toBe('La confirmacion no coincide con la nueva password.');
  });

  it('changes password, shows confirmation and logs out', fakeAsync(() => {
    authService.changePassword.and.returnValue(of({ message: 'ok' }));
    (component as any).changePasswordForm.setValue({
      currentPassword: 'TempPass1!',
      newPassword: 'ValidPass1!',
      confirmPassword: 'ValidPass1!'
    });

    (component as any).submit();
    tick(1800);

    expect(authService.changePassword).toHaveBeenCalledWith({
      currentPassword: 'TempPass1!',
      newPassword: 'ValidPass1!'
    });
    expect((component as any).successMessage()).toBe(
      'Password cambiada correctamente. Vuelve a iniciar sesion con tu nueva password.'
    );
    expect(authService.logout).toHaveBeenCalled();
  }));

  it('shows backend errors', () => {
    authService.changePassword.and.returnValue(
      throwError(() => ({ error: { error: 'Password temporal expirada' } }))
    );
    (component as any).changePasswordForm.setValue({
      currentPassword: 'TempPass1!',
      newPassword: 'ValidPass1!',
      confirmPassword: 'ValidPass1!'
    });

    (component as any).submit();

    expect((component as any).errorMessage()).toBe('Password temporal expirada');
    expect((component as any).isSubmitting()).toBeFalse();
  });
});
