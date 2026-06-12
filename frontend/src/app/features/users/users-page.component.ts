import { CommonModule } from '@angular/common';
import { Component, inject, OnInit, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';

import { UserRole } from '../../core/models/auth.models';
import { UserAdminResponse, UserStatus } from '../../core/models/user-admin.models';
import { UserAdminService } from '../../core/services/user-admin.service';

@Component({
  selector: 'app-users-page',
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './users-page.component.html',
  styleUrl: './users-page.component.scss'
})
export class UsersPageComponent implements OnInit {
  private readonly formBuilder = inject(FormBuilder);
  private readonly userAdminService = inject(UserAdminService);

  protected readonly users = signal<UserAdminResponse[]>([]);
  protected readonly isLoading = signal(false);
  protected readonly isSubmitting = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly editingUserId = signal<number | null>(null);
  protected readonly availableRoles: UserRole[] = ['ADMIN', 'EDITOR'];

  protected readonly userForm = this.formBuilder.nonNullable.group({
    email: ['', [Validators.required, Validators.email]],
    name: ['', [Validators.required, Validators.minLength(3)]],
    role: ['EDITOR' as UserRole, [Validators.required]]
  });

  ngOnInit(): void {
    this.loadUsers();
  }

  protected loadUsers(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.userAdminService.listUsers().subscribe({
      next: (users) => {
        this.users.set(users);
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la lista de usuarios.');
        this.isLoading.set(false);
      }
    });
  }

  protected startCreate(): void {
    this.editingUserId.set(null);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.userForm.reset({
      email: '',
      name: '',
      role: 'EDITOR'
    });
    this.userForm.controls.email.enable();
  }

  protected startEdit(user: UserAdminResponse): void {
    this.editingUserId.set(user.id);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.userForm.reset({
      email: user.email,
      name: user.name,
      role: user.role
    });
    this.userForm.controls.email.disable();
  }

  protected submit(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    this.isSubmitting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    const editingUserId = this.editingUserId();
    const payload = this.userForm.getRawValue();

    if (editingUserId === null) {
      this.userAdminService.createUser({
        email: payload.email,
        name: payload.name,
        role: payload.role
      }).subscribe({
        next: () => {
          this.isSubmitting.set(false);
          this.successMessage.set('Usuario creado. Se ha enviado una password temporal por email.');
          this.startCreateKeepingMessage();
          this.loadUsers();
        },
        error: (error: { error?: { error?: string } }) => {
          this.isSubmitting.set(false);
          this.errorMessage.set(error.error?.error ?? 'No se pudo crear el usuario.');
        }
      });
      return;
    }

    this.userAdminService.updateUser(editingUserId, {
      name: payload.name,
      role: payload.role
    }).subscribe({
      next: () => {
        this.isSubmitting.set(false);
        this.successMessage.set('Usuario actualizado correctamente.');
        this.startCreateKeepingMessage();
        this.loadUsers();
      },
      error: (error: { error?: { error?: string } }) => {
        this.isSubmitting.set(false);
        this.errorMessage.set(error.error?.error ?? 'No se pudo actualizar el usuario.');
      }
    });
  }

  protected changeStatus(userId: number, action: 'activate' | 'disable' | 'lock' | 'unlock'): void {
    this.successMessage.set(null);
    this.errorMessage.set(null);

    const request = {
      activate: () => this.userAdminService.activateUser(userId),
      disable: () => this.userAdminService.disableUser(userId),
      lock: () => this.userAdminService.lockUser(userId),
      unlock: () => this.userAdminService.unlockUser(userId)
    }[action];

    request().subscribe({
      next: () => {
        this.successMessage.set(this.successMessageForAction(action));
        this.loadUsers();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo actualizar el estado del usuario.');
      }
    });
  }

  protected resetTemporaryPassword(userId: number): void {
    this.successMessage.set(null);
    this.errorMessage.set(null);

    this.userAdminService.resetTemporaryPassword(userId).subscribe({
      next: () => {
        this.successMessage.set('Password temporal regenerada y enviada por email.');
        this.loadUsers();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo regenerar la password temporal.');
      }
    });
  }

  protected statusLabel(status: UserStatus): string {
    const labels: Record<UserStatus, string> = {
      PENDING_ACTIVATION: 'Pendiente activacion',
      ACTIVE: 'Activo',
      INACTIVE: 'Inactivo',
      LOCKED: 'Bloqueado'
    };
    return labels[status];
  }

  protected formatDate(value: string | null): string {
    if (value === null) {
      return '-';
    }

    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  private startCreateKeepingMessage(): void {
    this.editingUserId.set(null);
    this.userForm.reset({
      email: '',
      name: '',
      role: 'EDITOR'
    });
    this.userForm.controls.email.enable();
  }

  private successMessageForAction(action: 'activate' | 'disable' | 'lock' | 'unlock'): string {
    const messages = {
      activate: 'Usuario activado correctamente.',
      disable: 'Usuario desactivado. Se ha enviado una notificacion por email.',
      lock: 'Usuario bloqueado. Se ha enviado una notificacion por email.',
      unlock: 'Usuario desbloqueado correctamente.'
    };
    return messages[action];
  }
}
