import { CommonModule } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, FormsModule, ReactiveFormsModule, Validators } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { DialogModule } from 'primeng/dialog';
import { InputTextModule } from 'primeng/inputtext';
import { MessageModule } from 'primeng/message';
import { SelectModule } from 'primeng/select';

import { UserRole } from '../../core/models/auth.models';
import { UserAdminResponse, UserStatus } from '../../core/models/user-admin.models';
import { UserAdminService } from '../../core/services/user-admin.service';
import { StandardTableComponent } from '../../shared/components/standard-table/standard-table.component';

type UserSortColumn =
  | 'id'
  | 'name'
  | 'email'
  | 'role'
  | 'status'
  | 'lastLoginAt'
  | 'lastPasswordChangeAt'
  | 'temporaryPasswordExpiresAt';
type SortDirection = 'asc' | 'desc';
type UserFormMode = 'create' | 'edit';

@Component({
  selector: 'app-users-page',
  imports: [ButtonModule, CommonModule, DialogModule, FormsModule, InputTextModule, MessageModule, ReactiveFormsModule, SelectModule, StandardTableComponent],
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
  protected readonly isUserModalOpen = signal(false);
  protected readonly formMode = signal<UserFormMode>('create');
  protected readonly deletingUser = signal<UserAdminResponse | null>(null);
  protected readonly isDeleting = signal(false);
  protected readonly globalFilter = signal('');
  protected readonly idFilter = signal('');
  protected readonly nameFilter = signal('');
  protected readonly emailFilter = signal('');
  protected readonly roleFilter = signal('');
  protected readonly statusFilter = signal('');
  protected readonly lastLoginAtFilter = signal('');
  protected readonly lastPasswordChangeAtFilter = signal('');
  protected readonly temporaryPasswordExpiresAtFilter = signal('');
  protected readonly sortColumn = signal<UserSortColumn>('name');
  protected readonly sortDirection = signal<SortDirection>('asc');
  protected readonly pageSize = signal(10);
  protected readonly currentPage = signal(1);
  protected readonly availableRoles: UserRole[] = ['ADMIN', 'EDITOR'];
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly statusOptions: UserStatus[] = ['PENDING_ACTIVATION', 'ACTIVE', 'INACTIVE', 'LOCKED'];
  protected readonly displayedUsers = computed(() => this.sortUsers(this.filterUsers(this.users())));
  protected readonly totalPages = computed(() => Math.max(1, Math.ceil(this.displayedUsers().length / this.pageSize())));
  protected readonly paginatedUsers = computed(() => {
    const page = Math.min(this.currentPage(), this.totalPages());
    const start = (page - 1) * this.pageSize();
    return this.displayedUsers().slice(start, start + this.pageSize());
  });

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
        this.currentPage.set(Math.min(this.currentPage(), this.totalPages()));
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
    this.formMode.set('create');
    this.isUserModalOpen.set(true);
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
    this.formMode.set('edit');
    this.isUserModalOpen.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);
    this.userForm.reset({
      email: user.email,
      name: user.name,
      role: user.role
    });
    this.userForm.controls.email.disable();
  }

  protected closeUserModal(): void {
    if (this.isSubmitting()) {
      return;
    }

    this.isUserModalOpen.set(false);
    this.editingUserId.set(null);
    this.formMode.set('create');
    this.userForm.reset({
      email: '',
      name: '',
      role: 'EDITOR'
    });
    this.userForm.controls.email.enable();
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
          this.closeUserModalKeepingMessage();
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
        this.successMessage.set('Usuario actualizado correctamente. Se ha enviado una notificacion por email.');
        this.closeUserModalKeepingMessage();
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

  protected startDelete(user: UserAdminResponse): void {
    this.deletingUser.set(user);
    this.successMessage.set(null);
    this.errorMessage.set(null);
  }

  protected closeDeleteModal(): void {
    if (this.isDeleting()) {
      return;
    }

    this.deletingUser.set(null);
  }

  protected confirmDelete(): void {
    const user = this.deletingUser();
    if (user === null) {
      return;
    }

    this.isDeleting.set(true);
    this.successMessage.set(null);
    this.errorMessage.set(null);

    this.userAdminService.deleteUser(user.id).subscribe({
      next: () => {
        this.isDeleting.set(false);
        this.deletingUser.set(null);
        this.successMessage.set('Usuario eliminado definitivamente de la base de datos. Se ha enviado una notificacion por email.');
        this.loadUsers();
      },
      error: (error: { error?: { error?: string } }) => {
        this.isDeleting.set(false);
        this.deletingUser.set(null);
        this.errorMessage.set(error.error?.error ?? 'No se pudo eliminar el usuario.');
      }
    });
  }

  protected setGlobalFilter(value: string): void {
    this.globalFilter.set(value);
    this.resetPagination();
  }

  protected setIdFilter(value: string): void {
    this.idFilter.set(value);
    this.resetPagination();
  }

  protected setNameFilter(value: string): void {
    this.nameFilter.set(value);
    this.resetPagination();
  }

  protected setEmailFilter(value: string): void {
    this.emailFilter.set(value);
    this.resetPagination();
  }

  protected setRoleFilter(value: string): void {
    this.roleFilter.set(value);
    this.resetPagination();
  }

  protected setStatusFilter(value: string): void {
    this.statusFilter.set(value);
    this.resetPagination();
  }

  protected setLastLoginAtFilter(value: string): void {
    this.lastLoginAtFilter.set(value);
    this.resetPagination();
  }

  protected setLastPasswordChangeAtFilter(value: string): void {
    this.lastPasswordChangeAtFilter.set(value);
    this.resetPagination();
  }

  protected setTemporaryPasswordExpiresAtFilter(value: string): void {
    this.temporaryPasswordExpiresAtFilter.set(value);
    this.resetPagination();
  }

  protected changeSort(column: UserSortColumn): void {
    if (this.sortColumn() === column) {
      this.sortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }

    this.sortColumn.set(column);
    this.sortDirection.set(this.isDateColumn(column) ? 'desc' : 'asc');
  }

  protected sortLabel(column: UserSortColumn): string {
    if (this.sortColumn() !== column) {
      return '';
    }

    return this.sortDirection() === 'asc' ? 'ASC' : 'DESC';
  }

  protected setPageSize(value: string): void {
    this.pageSize.set(Number(value));
    this.resetPagination();
  }

  protected goToPreviousPage(): void {
    this.currentPage.update((page) => Math.max(1, page - 1));
  }

  protected goToNextPage(): void {
    this.currentPage.update((page) => Math.min(this.totalPages(), page + 1));
  }

  protected statusDescription(status: UserStatus): string {
    const descriptions: Record<UserStatus, string> = {
      PENDING_ACTIVATION: 'Password temporal pendiente de cambio.',
      ACTIVE: 'Puede iniciar sesion y operar segun su rol.',
      INACTIVE: 'Baja administrativa: no puede iniciar sesion hasta reactivacion.',
      LOCKED: 'Bloqueo reversible por incidencia: no puede iniciar sesion hasta desbloqueo.'
    };
    return descriptions[status];
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

  private closeUserModalKeepingMessage(): void {
    this.isUserModalOpen.set(false);
    this.editingUserId.set(null);
    this.formMode.set('create');
    this.userForm.reset({
      email: '',
      name: '',
      role: 'EDITOR'
    });
    this.userForm.controls.email.enable();
  }

  private filterUsers(users: UserAdminResponse[]): UserAdminResponse[] {
    return users.filter((user) => this.matchesGlobalFilter(user))
      .filter((user) => this.matchesText(user.id.toString(), this.idFilter()))
      .filter((user) => this.matchesText(user.name, this.nameFilter()))
      .filter((user) => this.matchesText(user.email, this.emailFilter()))
      .filter((user) => this.matchesSelect(user.role, this.roleFilter()))
      .filter((user) => this.matchesSelect(user.status, this.statusFilter()))
      .filter((user) => this.matchesText(this.formatDate(user.lastLoginAt), this.lastLoginAtFilter()))
      .filter((user) => this.matchesText(this.formatDate(user.lastPasswordChangeAt), this.lastPasswordChangeAtFilter()))
      .filter((user) => this.matchesText(this.formatDate(user.temporaryPasswordExpiresAt), this.temporaryPasswordExpiresAtFilter()));
  }

  private matchesGlobalFilter(user: UserAdminResponse): boolean {
    const filter = this.normalize(this.globalFilter());
    if (!filter) {
      return true;
    }

    return [
      user.id.toString(),
      `#${user.id}`,
      user.name,
      user.email,
      user.role,
      user.status,
      this.statusLabel(user.status),
      this.formatDate(user.lastLoginAt),
      this.formatDate(user.lastPasswordChangeAt),
      this.formatDate(user.temporaryPasswordExpiresAt)
    ].some((value) => this.normalize(value).includes(filter));
  }

  private sortUsers(users: UserAdminResponse[]): UserAdminResponse[] {
    const direction = this.sortDirection() === 'asc' ? 1 : -1;
    const column = this.sortColumn();

    return [...users].sort((left, right) => direction * this.compareUsers(left, right, column));
  }

  private compareUsers(left: UserAdminResponse, right: UserAdminResponse, column: UserSortColumn): number {
    if (column === 'id') {
      return left.id - right.id;
    }

    if (this.isDateColumn(column)) {
      return this.dateValue(left[column]) - this.dateValue(right[column]);
    }

    return left[column].localeCompare(right[column], 'es', { sensitivity: 'base' });
  }

  private matchesText(value: string, filter: string): boolean {
    const normalizedFilter = this.normalize(filter);
    return !normalizedFilter || this.normalize(value).includes(normalizedFilter);
  }

  private matchesSelect(value: string, filter: string): boolean {
    return !filter || value === filter;
  }

  private normalize(value: string): string {
    return value.trim().toLocaleLowerCase('es');
  }

  private resetPagination(): void {
    this.currentPage.set(1);
  }

  private isDateColumn(column: UserSortColumn): column is 'lastLoginAt' | 'lastPasswordChangeAt' | 'temporaryPasswordExpiresAt' {
    return column === 'lastLoginAt' || column === 'lastPasswordChangeAt' || column === 'temporaryPasswordExpiresAt';
  }

  private dateValue(value: string | null): number {
    return value === null ? 0 : new Date(value).getTime();
  }

  private successMessageForAction(action: 'activate' | 'disable' | 'lock' | 'unlock'): string {
    const messages = {
      activate: 'Usuario activado correctamente. Se ha enviado una notificacion por email.',
      disable: 'Usuario desactivado. Se ha enviado una notificacion por email.',
      lock: 'Usuario bloqueado. Se ha enviado una notificacion por email.',
      unlock: 'Usuario desbloqueado correctamente. Se ha enviado una notificacion por email.'
    };
    return messages[action];
  }
}
