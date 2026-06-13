import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-forgot-password-page',
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: './forgot-password-page.component.html',
  styleUrl: './forgot-password-page.component.scss'
})
export class ForgotPasswordPageComponent {
  private readonly formBuilder = inject(FormBuilder);
  private readonly authService = inject(AuthService);

  protected readonly isSubmitting = signal(false);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly errorMessage = signal<string | null>(null);

  protected readonly forgotPasswordForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]]
  });

  protected submit(): void {
    if (this.forgotPasswordForm.invalid) {
      this.forgotPasswordForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    this.authService.requestPasswordReset(this.forgotPasswordForm.getRawValue()).subscribe({
      next: (response) => {
        this.isSubmitting.set(false);
        this.successMessage.set(response.message);
      },
      error: (error: { error?: { error?: string } }) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(error.error?.error ?? 'No se pudo procesar la solicitud.');
      }
    });
  }
}
