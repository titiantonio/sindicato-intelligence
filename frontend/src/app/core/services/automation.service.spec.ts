import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { AutomationService } from './automation.service';

describe('AutomationService', () => {
  let service: AutomationService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(AutomationService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('runs pending classifications through backend API', () => {
    service.runClassifications().subscribe();

    const request = httpTestingController.expectOne('/api/v1/automation/classifications/run');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.flush({ processedCount: 0, successCount: 0, failedCount: 0, skippedCount: 0, errors: [] });
  });

  it('runs pending event detection through backend API', () => {
    service.runEventDetection().subscribe();

    const request = httpTestingController.expectOne('/api/v1/automation/events/run');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({});
    request.flush({ processedCount: 0, successCount: 0, failedCount: 0, skippedCount: 0, errors: [] });
  });

  it('runs event analysis with optional event id through backend API', () => {
    service.runAnalysis(12).subscribe();

    const request = httpTestingController.expectOne('/api/v1/automation/analysis/run');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ eventId: 12 });
    request.flush({ processedCount: 1, successCount: 1, failedCount: 0, skippedCount: 0, errors: [] });
  });

  it('lists automation workflow settings', () => {
    service.listSettings().subscribe();

    const request = httpTestingController.expectOne('/api/v1/automation/settings');
    expect(request.request.method).toBe('GET');
    request.flush([]);
  });

  it('updates automation workflow settings', () => {
    const payload = { enabled: true, intervalSeconds: 600, batchSize: 1 };

    service.updateSetting('WF02_CLASSIFICATION', payload).subscribe();

    const request = httpTestingController.expectOne('/api/v1/automation/settings/WF02_CLASSIFICATION');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(payload);
    request.flush({ workflowCode: 'WF02_CLASSIFICATION', ...payload });
  });

  it('runs a configured workflow from settings endpoint', () => {
    service.runWorkflow('WF03_EVENT_DETECTION').subscribe();

    const request = httpTestingController.expectOne('/api/v1/automation/settings/WF03_EVENT_DETECTION/run');
    expect(request.request.method).toBe('POST');
    request.flush({ processedCount: 0, successCount: 0, failedCount: 0, skippedCount: 0, errors: [] });
  });
});
