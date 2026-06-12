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
      'resetTemporaryPassword'
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
    expect((component as any).successMessage()).toBe(
      'Usuario creado. Se ha enviado una password temporal por email.'
    );
  });

  it('prepares and submits user edits without changing email', () => {
    userAdminService.updateUser.and.returnValue(of({ ...user, name: 'Editor Senior', role: 'ADMIN' }));

    (component as any).startEdit(user);

    expect((component as any).editingUserId()).toBe(user.id);
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
