import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { AnalysisService } from './analysis.service';

describe('AnalysisService', () => {
  let service: AnalysisService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(AnalysisService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('generates analysis for an event', () => {
    service.generateAnalysis(7).subscribe();

    const request = httpTestingController.expectOne('/api/v1/analysis/generate');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ eventId: 7 });
    request.flush({ id: 3, eventId: 7 });
  });
});
