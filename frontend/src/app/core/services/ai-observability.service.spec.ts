import { TestBed } from '@angular/core/testing';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { provideHttpClient } from '@angular/common/http';

import { AiObservabilityService } from './ai-observability.service';

describe('AiObservabilityService', () => {
  let service: AiObservabilityService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });
    service = TestBed.inject(AiObservabilityService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('lists prompt versions', () => {
    service.listPrompts().subscribe((response) => {
      expect(response[0].promptKey).toBe('WF02_CLASSIFICATION');
    });

    const request = httpTestingController.expectOne('/api/v1/ai/prompts');
    expect(request.request.method).toBe('GET');
    request.flush([{ promptKey: 'WF02_CLASSIFICATION' }]);
  });

  it('lists ai metrics with limit', () => {
    service.listMetrics(10).subscribe((response) => {
      expect(response.totalOperations).toBe(1);
    });

    const request = httpTestingController.expectOne('/api/v1/ai/metrics?limit=10');
    expect(request.request.method).toBe('GET');
    request.flush({ totalOperations: 1, successCount: 1, failedCount: 0, averageLatencyMs: 120, recentMetrics: [] });
  });

  it('lists daily ai metrics by date', () => {
    service.listDailyMetrics('2026-06-18').subscribe((response) => {
      expect(response.totalOperations).toBe(2);
    });

    const request = httpTestingController.expectOne('/api/v1/ai/metrics?date=2026-06-18');
    expect(request.request.method).toBe('GET');
    request.flush({ totalOperations: 2, successCount: 1, failedCount: 1, averageLatencyMs: 120, recentMetrics: [] });
  });
});
