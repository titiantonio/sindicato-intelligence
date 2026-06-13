import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { AuditService } from './audit.service';

describe('AuditService', () => {
  let service: AuditService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(AuditService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('loads user audit', () => {
    service.listUserAudit(25).subscribe();

    const request = httpTestingController.expectOne('/api/v1/audit/users?limit=25');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('loads editorial audit', () => {
    service.listEditorialAudit(50).subscribe();

    const request = httpTestingController.expectOne('/api/v1/audit/editorial?limit=50');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });
});
