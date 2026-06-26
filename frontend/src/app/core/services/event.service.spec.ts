import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { EventService } from './event.service';

describe('EventService', () => {
  let service: EventService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(EventService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('merges events', () => {
    service.mergeEvents(10, [11, 12]).subscribe();

    const request = httpTestingController.expectOne('/api/v1/events/merge');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ targetEventId: 10, sourceEventIds: [11, 12] });
    request.flush({ id: 10 });
  });

  it('discards events', () => {
    service.discardEvent(10).subscribe();

    const request = httpTestingController.expectOne('/api/v1/events/10/discard');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.flush({ id: 10 });
  });

  it('restores manually discarded events', () => {
    service.restoreEvent(10).subscribe();

    const request = httpTestingController.expectOne('/api/v1/events/10/restore');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.flush({ id: 10 });
  });
});
