import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { UserAdminResponse } from '../models/user-admin.models';
import { UserAdminService } from './user-admin.service';

describe('UserAdminService', () => {
  let service: UserAdminService;
  let httpTestingController: HttpTestingController;

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

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(UserAdminService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('lists users', () => {
    service.listUsers().subscribe((response) => expect(response).toEqual([user]));

    const request = httpTestingController.expectOne('/api/v1/users');
    expect(request.request.method).toBe('GET');
    request.flush([user]);
  });

  it('creates users without password', () => {
    service.createUser({ email: user.email, name: user.name, role: user.role }).subscribe();

    const request = httpTestingController.expectOne('/api/v1/users');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({
      email: user.email,
      name: user.name,
      role: user.role
    });
    expect(request.request.body.password).toBeUndefined();
    request.flush(user);
  });

  it('updates user name and role', () => {
    service.updateUser(user.id, { name: 'Editor Senior', role: 'ADMIN' }).subscribe();

    const request = httpTestingController.expectOne('/api/v1/users/7');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual({ name: 'Editor Senior', role: 'ADMIN' });
    request.flush({ ...user, name: 'Editor Senior', role: 'ADMIN' });
  });

  it('calls status and reset actions', () => {
    const actions = [
      { run: () => service.activateUser(7).subscribe(), url: '/api/v1/users/7/activate' },
      { run: () => service.disableUser(7).subscribe(), url: '/api/v1/users/7/disable' },
      { run: () => service.lockUser(7).subscribe(), url: '/api/v1/users/7/lock' },
      { run: () => service.unlockUser(7).subscribe(), url: '/api/v1/users/7/unlock' },
      {
        run: () => service.resetTemporaryPassword(7).subscribe(),
        url: '/api/v1/users/7/reset-temporary-password'
      }
    ];

    actions.forEach((action) => {
      action.run();
      const request = httpTestingController.expectOne(action.url);
      expect(request.request.method).toBe('POST');
      expect(request.request.body).toEqual({});
      request.flush(user);
    });
  });

  it('deletes users', () => {
    service.deleteUser(user.id).subscribe((response) => expect(response).toBeNull());

    const request = httpTestingController.expectOne('/api/v1/users/7');
    expect(request.request.method).toBe('DELETE');
    request.flush(null);
  });
});
