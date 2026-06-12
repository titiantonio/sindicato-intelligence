import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { AuthService } from '../../../core/services/auth.service';
import { PASSWORD_PATTERN } from '../password-pattern';

@Component({
  selector: 'app-change-password-page',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './change-password-page.component.html',
  styleUrl: './change-password-page.component.scss'
})
export class ChangePasswordPageComponent {
  private readonly authService = inject(AuthService);
  private readonly formBuilder = inject(FormBuilder);

  protected readonly isSubmitting = signal(false);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly changePasswordForm = this.formBuilder.nonNullable.group({
    currentPassword: ['', [Validators.required]],
    newPassword: ['', [Validators.required, Validators.pattern(PASSWORD_PATTERN)]],
    confirmPassword: ['', [Validators.required]]
  });

  protected submit(): void {
    if (this.changePasswordForm.invalid) {
      this.changePasswordForm.markAllAsTouched();
      return;
    }

    const { currentPassword, newPassword, confirmPassword } = this.changePasswordForm.getRawValue();
    if (newPassword !== confirmPassword) {
      this.errorMessage.set('La confirmacion no coincide con la nueva password.');
      return;
    }

    this.isSubmitting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    this.authService.changePassword({ currentPassword, newPassword }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.successMessage.set('Password cambiada correctamente. Vuelve a iniciar sesion con tu nueva password.');
        setTimeout(() => this.authService.logout(), 1800);
      },
      error: (error: { error?: { error?: string } }) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(error.error?.error ?? 'No se pudo cambiar la password.');
      }
    });
  }
}
