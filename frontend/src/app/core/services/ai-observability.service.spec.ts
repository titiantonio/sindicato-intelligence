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

  it('manages ai provider settings', () => {
    service.listProviders().subscribe((response) => {
      expect(response[0].providerCode).toBe('gemini');
    });
    let request = httpTestingController.expectOne('/api/v1/ai/providers');
    expect(request.request.method).toBe('GET');
    request.flush([{ providerCode: 'gemini' }]);

    service.updateProvider('gemini', { enabled: true, apiKey: 'test-key' }).subscribe();
    request = httpTestingController.expectOne('/api/v1/ai/providers/gemini');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual({ enabled: true, apiKey: 'test-key' });
    request.flush({ providerCode: 'gemini' });

    service.listProviderModels('gemini', 'test-key').subscribe((response) => {
      expect(response[0].name).toBe('models/gemini-2.5-flash');
    });
    request = httpTestingController.expectOne('/api/v1/ai/providers/gemini/models');
    expect(request.request.method).toBe('POST');
    expect(request.request.body).toEqual({ apiKey: 'test-key' });
    request.flush([{ name: 'models/gemini-2.5-flash', displayName: 'Gemini 2.5 Flash' }]);
  });

  it('manages ai workflow settings', () => {
    service.listWorkflowSettings().subscribe((response) => {
      expect(response[0].workflowCode).toBe('WF04_ANALYSIS');
    });
    let request = httpTestingController.expectOne('/api/v1/ai/workflow-settings');
    expect(request.request.method).toBe('GET');
    request.flush([{ workflowCode: 'WF04_ANALYSIS' }]);

    service.updateWorkflowSetting('WF04_ANALYSIS', {
      providerCode: 'gemini',
      modelName: 'models/gemini-2.5-flash',
      temperature: 0.3,
      maxOutputTokens: 2048,
      cooldownSeconds: 60
    }).subscribe();
    request = httpTestingController.expectOne('/api/v1/ai/workflow-settings/WF04_ANALYSIS');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual({
      providerCode: 'gemini',
      modelName: 'models/gemini-2.5-flash',
      temperature: 0.3,
      maxOutputTokens: 2048,
      cooldownSeconds: 60
    });
    request.flush({ workflowCode: 'WF04_ANALYSIS' });
  });
});
