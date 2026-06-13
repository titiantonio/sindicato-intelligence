import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { PublicationService } from './publication.service';

describe('PublicationService', () => {
  let service: PublicationService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(PublicationService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('schedules a publication', () => {
    service.schedulePublication(7, '2026-06-14T10:00:00.000Z').subscribe();

    const request = httpTestingController.expectOne('/api/v1/publications/7/schedule');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ scheduledAt: '2026-06-14T10:00:00.000Z' });
    request.flush({ id: 9, contentId: 7, status: 'SCHEDULED' });
  });
});
