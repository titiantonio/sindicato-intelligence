import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of, throwError } from 'rxjs';

import { UserAdminResponse } from '../../core/models/user-admin.models';
import { UserAdminService } from '../../core/services/user-admin.service';
import { UsersPageComponent } from './users-page.component';

describe('UsersPageComponent', () => {
  let fixture: ComponentFixture<UsersPageComponent>;
  let component: UsersPageComponent;
  let userAdminService: jasmine.SpyObj<UserAdminService>;

  const user: UserAdminResponse = {
    id: 7,
    email: 'editor@sindicato.es',
    name: 'Editor',
    role: 'EDITOR',
    active: true,
    mustChangePassword: true,
    status: 'PENDING_ACTIVATION',
    temporaryPasswordExpiresAt: '2026-06-20T10:00:00Z',
    lastLoginAt: null,
    lastPasswordChangeAt: null
  };

  beforeEach(async () => {
    userAdminService = jasmine.createSpyObj<UserAdminService>('UserAdminService', [
      'listUsers',
      'createUser',
      'updateUser',
      'activateUser',
      'disableUser',
      'lockUser',
      'unlockUser',
      'resetTemporaryPassword',
      'deleteUser'
    ]);
    userAdminService.listUsers.and.returnValue(of([user]));

    await TestBed.configureTestingModule({
      imports: [UsersPageComponent],
      providers: [{ provide: UserAdminService, useValue: userAdminService }]
    }).compileComponents();

    fixture = TestBed.createComponent(UsersPageComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('loads users on init', () => {
    expect(userAdminService.listUsers).toHaveBeenCalled();
    expect((component as any).users()).toEqual([user]);
    expect((component as any).isLoading()).toBeFalse();
  });

  it('creates users without password', () => {
    userAdminService.createUser.and.returnValue(of(user));
    (component as any).startCreate();
    (component as any).userForm.setValue({
      email: 'new-editor@sindicato.es',
      name: 'New Editor',
      role: 'EDITOR'
    });

    (component as any).submit();

    expect(userAdminService.createUser).toHaveBeenCalledWith({
      email: 'new-editor@sindicato.es',
      name: 'New Editor',
      role: 'EDITOR'
    });
    expect((userAdminService.createUser.calls.mostRecent().args[0] as any).password).toBeUndefined();
    expect((component as any).isUserModalOpen()).toBeFalse();
    expect((component as any).successMessage()).toBe(
      'Usuario creado. Se ha enviado una password temporal por email.'
    );
  });

  it('prepares and submits user edits without changing email', () => {
    userAdminService.updateUser.and.returnValue(of({ ...user, name: 'Editor Senior', role: 'ADMIN' }));

    (component as any).startEdit(user);

    expect((component as any).editingUserId()).toBe(user.id);
    expect((component as any).isUserModalOpen()).toBeTrue();
    expect((component as any).formMode()).toBe('edit');
    expect((component as any).userForm.controls.email.disabled).toBeTrue();

    (component as any).userForm.patchValue({
      name: 'Editor Senior',
      role: 'ADMIN'
    });
    (component as any).submit();

    expect(userAdminService.updateUser).toHaveBeenCalledWith(user.id, {
      name: 'Editor Senior',
      role: 'ADMIN'
    });
    expect((component as any).successMessage()).toBe('Usuario actualizado correctamente.');
  });

  it('filters, sorts and paginates users locally', () => {
    const admin: UserAdminResponse = {
      ...user,
      id: 2,
      email: 'admin@sindicato.es',
      name: 'Admin',
      role: 'ADMIN',
      status: 'ACTIVE'
    };
    (component as any).users.set([user, admin]);

    (component as any).setGlobalFilter('admin');
    expect((component as any).displayedUsers()).toEqual([admin]);

    (component as any).setGlobalFilter('');
    (component as any).changeSort('id');
    expect((component as any).displayedUsers().map((listedUser: UserAdminResponse) => listedUser.id)).toEqual([2, 7]);

    (component as any).setPageSize('1');
    expect((component as any).paginatedUsers()).toEqual([admin]);

    (component as any).goToNextPage();
    expect((component as any).paginatedUsers()).toEqual([user]);
  });

  it('does not submit invalid forms', () => {
    (component as any).userForm.setValue({
      email: 'bad-email',
      name: 'Ed',
      role: 'EDITOR'
    });

    (component as any).submit();

    expect(userAdminService.createUser).not.toHaveBeenCalled();
    expect((component as any).userForm.touched).toBeTrue();
  });

  it('calls account status actions and shows success messages', () => {
    userAdminService.activateUser.and.returnValue(of({ ...user, status: 'ACTIVE' }));
    userAdminService.disableUser.and.returnValue(of({ ...user, status: 'INACTIVE' }));
    userAdminService.lockUser.and.returnValue(of({ ...user, status: 'LOCKED' }));
    userAdminService.unlockUser.and.returnValue(of({ ...user, status: 'ACTIVE' }));

    (component as any).changeStatus(user.id, 'activate');
    expect(userAdminService.activateUser).toHaveBeenCalledWith(user.id);
    expect((component as any).successMessage()).toBe('Usuario activado correctamente.');

    (component as any).changeStatus(user.id, 'disable');
    expect(userAdminService.disableUser).toHaveBeenCalledWith(user.id);
    expect((component as any).successMessage()).toBe(
      'Usuario desactivado. Se ha enviado una notificacion por email.'
    );

    (component as any).changeStatus(user.id, 'lock');
    expect(userAdminService.lockUser).toHaveBeenCalledWith(user.id);
    expect((component as any).successMessage()).toBe(
      'Usuario bloqueado. Se ha enviado una notificacion por email.'
    );

    (component as any).changeStatus(user.id, 'unlock');
    expect(userAdminService.unlockUser).toHaveBeenCalledWith(user.id);
    expect((component as any).successMessage()).toBe('Usuario desbloqueado correctamente.');
  });

  it('resets temporary password and reloads users', () => {
    userAdminService.resetTemporaryPassword.and.returnValue(of(user));

    (component as any).resetTemporaryPassword(user.id);

    expect(userAdminService.resetTemporaryPassword).toHaveBeenCalledWith(user.id);
    expect((component as any).successMessage()).toBe(
      'Password temporal regenerada y enviada por email.'
    );
  });

  it('confirms user deletion before calling the service', () => {
    userAdminService.deleteUser.and.returnValue(of(undefined));

    (component as any).startDelete(user);

    expect((component as any).deletingUser()).toEqual(user);

    (component as any).confirmDelete();

    expect(userAdminService.deleteUser).toHaveBeenCalledWith(user.id);
    expect((component as any).deletingUser()).toBeNull();
    expect((component as any).successMessage()).toBe(
      'Usuario eliminado definitivamente de la base de datos.'
    );
  });

  it('shows dependency conflicts when deleting users', () => {
    userAdminService.deleteUser.and.returnValue(
      throwError(() => ({ error: { error: 'No se puede eliminar el usuario porque conserva referencias funcionales: generated_content.created_by=1' } }))
    );

    (component as any).startDelete(user);
    (component as any).confirmDelete();

    expect((component as any).errorMessage()).toBe(
      'No se puede eliminar el usuario porque conserva referencias funcionales: generated_content.created_by=1'
    );
    expect((component as any).isDeleting()).toBeFalse();
  });

  it('shows service errors', () => {
    userAdminService.createUser.and.returnValue(
      throwError(() => ({ error: { error: 'Email duplicado' } }))
    );
    (component as any).userForm.setValue({
      email: 'editor@sindicato.es',
      name: 'Editor',
      role: 'EDITOR'
    });

    (component as any).submit();

    expect((component as any).errorMessage()).toBe('Email duplicado');
    expect((component as any).isSubmitting()).toBeFalse();
  });
});
