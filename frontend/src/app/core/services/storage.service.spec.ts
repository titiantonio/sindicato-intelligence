import { TestBed } from '@angular/core/testing';

import { StorageService } from './storage.service';

describe('StorageService', () => {
  let service: StorageService;

  beforeEach(() => {
    localStorage.clear();

    TestBed.configureTestingModule({});
    service = TestBed.inject(StorageService);
  });

  afterEach(() => {
    localStorage.clear();
  });

  it('stores and reads JSON values', () => {
    service.setItem('session', { accessToken: 'token' });

    expect(service.getItem<{ accessToken: string }>('session')).toEqual({ accessToken: 'token' });
  });

  it('returns null when the key is missing', () => {
    expect(service.getItem('missing')).toBeNull();
  });

  it('removes stored values', () => {
    service.setItem('session', { accessToken: 'token' });

    service.removeItem('session');

    expect(service.getItem('session')).toBeNull();
  });
});
