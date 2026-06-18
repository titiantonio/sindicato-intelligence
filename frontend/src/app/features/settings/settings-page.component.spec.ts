import { ComponentFixture, TestBed } from '@angular/core/testing';
import { provideRouter } from '@angular/router';
import { of } from 'rxjs';

import { AutomationWorkflowSetting } from '../../core/models/automation.models';
import { AiObservabilityService } from '../../core/services/ai-observability.service';
import { ApplicationSettingsService } from '../../core/services/application-settings.service';
import { AutomationService } from '../../core/services/automation.service';
import { SettingsPageComponent } from './settings-page.component';

describe('SettingsPageComponent', () => {
  let fixture: ComponentFixture<SettingsPageComponent>;
  let aiObservabilityService: jasmine.SpyObj<AiObservabilityService>;
  let applicationSettingsService: jasmine.SpyObj<ApplicationSettingsService>;
  let automationService: jasmine.SpyObj<AutomationService>;

  beforeEach(async () => {
    aiObservabilityService = jasmine.createSpyObj<AiObservabilityService>('AiObservabilityService', ['listPrompts', 'listMetrics', 'listDailyMetrics']);
    applicationSettingsService = jasmine.createSpyObj<ApplicationSettingsService>('ApplicationSettingsService', ['getTelegramSettings', 'updateTelegramSettings']);
    automationService = jasmine.createSpyObj<AutomationService>('AutomationService', ['listSettings', 'updateSetting', 'runWorkflow', 'getOverview']);
    aiObservabilityService.listPrompts.and.returnValue(of(promptVersions()));
    aiObservabilityService.listMetrics.and.returnValue(of(metrics()));
    aiObservabilityService.listDailyMetrics.and.returnValue(of(metrics()));
    applicationSettingsService.getTelegramSettings.and.returnValue(of(telegramSettings()));
    applicationSettingsService.updateTelegramSettings.and.returnValue(of({ ...telegramSettings(), enabled: true, readyToPublish: true }));
    automationService.listSettings.and.returnValue(of([setting()]));
    automationService.updateSetting.and.returnValue(of(setting()));
    automationService.runWorkflow.and.returnValue(of({ processedCount: 1, successCount: 1, failedCount: 0, skippedCount: 0, errors: [] }));
    automationService.getOverview.and.returnValue(of({
      n8nWorkflowCode: 'WF01_CAPTURE_NEWS',
      n8nWorkflowName: 'WF-01-Capture-News',
      n8nStatus: 'EXTERNAL_N8N',
      backendEnabledCount: 1,
      backendFailedCount: 0,
      backendRunningCount: 0,
      backendWorkflows: [setting()]
    }));

    await TestBed.configureTestingModule({
      imports: [SettingsPageComponent],
      providers: [
        { provide: AiObservabilityService, useValue: aiObservabilityService },
        { provide: ApplicationSettingsService, useValue: applicationSettingsService },
        { provide: AutomationService, useValue: automationService },
        provideRouter([])
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(SettingsPageComponent);
    fixture.detectChanges();
  });

  it('renders ai tab with prompt and metric tables by default', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('IA y prompts');
    expect(compiled.textContent).toContain('Prompts versionados');
    expect(compiled.textContent).toContain('Metricas diarias');
    expect(compiled.textContent).toContain('Clasificacion de noticias');
    expect(compiled.textContent).toContain('GeminiAIProvider');
    expect(compiled.textContent).toContain('gemini-1.5-flash');
    expect(compiled.textContent).not.toContain('Guardar Telegram');
    expect(aiObservabilityService.listDailyMetrics).toHaveBeenCalled();
  });

  it('renders publication and automation configuration in separate tabs', () => {
    const component = fixture.componentInstance as any;
    const compiled = fixture.nativeElement as HTMLElement;

    component.setTab('publication');
    fixture.detectChanges();

    expect(compiled.textContent).toContain('Telegram');
    expect(compiled.textContent).toContain('Guardar Telegram');
    expect(compiled.textContent).not.toContain('WF02 - Clasificacion');

    component.setTab('automation');
    fixture.detectChanges();

    expect(compiled.textContent).toContain('WF02 - Clasificacion');
    expect(component.formFor('WF02_CLASSIFICATION').intervalMinutes).toBe(10);
  });

  it('saves edited settings in seconds', () => {
    (fixture.componentInstance as any).updateInterval('WF02_CLASSIFICATION', 5);
    (fixture.componentInstance as any).updateBatchSize('WF02_CLASSIFICATION', 2);
    (fixture.componentInstance as any).save(setting());

    expect(automationService.updateSetting).toHaveBeenCalledWith('WF02_CLASSIFICATION', {
      enabled: true,
      intervalSeconds: 300,
      batchSize: 2
    });
  });

  it('runs workflow manually', () => {
    (fixture.componentInstance as any).runNow(setting());

    expect(automationService.runWorkflow).toHaveBeenCalledWith('WF02_CLASSIFICATION');
  });

  it('saves telegram publication settings', () => {
    (fixture.componentInstance as any).updateTelegramForm({
      enabled: true,
      botToken: 'token',
      chatId: 'chat-id'
    });
    (fixture.componentInstance as any).saveTelegramSettings();

    expect(applicationSettingsService.updateTelegramSettings).toHaveBeenCalledWith({
      enabled: true,
      baseUrl: 'https://api.telegram.org',
      botToken: 'token',
      chatId: 'chat-id',
      disableWebPagePreview: true
    });
  });

  it('filters, sorts and paginates prompt versions', () => {
    const component = fixture.componentInstance as any;

    component.setPromptNameFilter('Analisis');
    expect(component.displayedPrompts().length).toBe(1);
    expect(component.displayedPrompts()[0].promptKey).toBe('WF04_ANALYSIS');

    component.setPromptNameFilter('');
    component.setPromptActiveFilter('Activo');
    expect(component.displayedPrompts().length).toBe(1);
    expect(component.displayedPrompts()[0].promptKey).toBe('WF02_CLASSIFICATION');

    component.setPromptActiveFilter('');
    component.changePromptSort('version');
    component.changePromptSort('version');
    expect(component.displayedPrompts()[0].version).toBe('1.1.0');

    component.setPromptPageSize('1');
    expect(component.paginatedPrompts().length).toBe(1);
    component.nextPromptPage();
    expect(component.promptDisplayPage()).toBe(2);
  });

  it('filters, sorts and paginates ai metrics', () => {
    const component = fixture.componentInstance as any;

    component.setMetricStatusFilter('Fallida');
    expect(component.displayedMetrics().length).toBe(1);
    expect(component.displayedMetrics()[0].status).toBe('FAILED');

    component.setMetricStatusFilter('');
    component.changeMetricSort('latencyMs');
    expect(component.displayedMetrics()[0].latencyMs).toBe(120);

    component.setMetricPageSize('1');
    expect(component.paginatedMetrics().length).toBe(1);
    component.nextMetricPage();
    expect(component.metricDisplayPage()).toBe(2);
  });

  it('renders daily metric cards with dashboard component data', () => {
    const component = fixture.componentInstance as any;

    expect(component.aiMetricCards().length).toBe(4);
    expect(component.aiMetricCards()[0].title).toBe('Operaciones IA');
    expect(component.aiMetricCards()[0].items[0].value).toBe(2);
    expect(component.aiMetricCards()[1].items[1].value).toBe(50);
  });

  it('changes daily metrics date', () => {
    const component = fixture.componentInstance as any;

    component.setMetricDate('2026-06-17');

    expect(aiObservabilityService.listDailyMetrics).toHaveBeenCalledWith('2026-06-17');
  });

  it('opens error and detail modals from metric rows', () => {
    const component = fixture.componentInstance as any;
    const failedMetric = metrics().recentMetrics[1];

    component.openMetricDetail(failedMetric);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Detalle de analisis IA');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Abrir evento relacionado');

    component.closeMetricDetail();
    component.openMetricError(new Event('click'), failedMetric);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Respuesta IA invalida');
  });

  function setting(): AutomationWorkflowSetting {
    return {
      workflowCode: 'WF02_CLASSIFICATION',
      enabled: true,
      intervalSeconds: 600,
      batchSize: 1,
      running: false,
      lastRunAt: null,
      lastSuccessAt: null,
      lastFailureAt: null,
      nextRunAt: '2026-06-16T10:10:00Z',
      lastProcessedCount: 0,
      lastSuccessCount: 0,
      lastFailedCount: 0,
      lastSkippedCount: 0,
      lastError: null
    };
  }

  function telegramSettings() {
    return {
      enabled: false,
      baseUrl: 'https://api.telegram.org',
      chatId: null,
      disableWebPagePreview: true,
      botTokenConfigured: false,
      botTokenPreview: null,
      readyToPublish: false,
      updatedAt: '2026-06-16T10:00:00Z'
    };
  }

  function promptVersions() {
    return [
      {
        promptKey: 'WF02_CLASSIFICATION',
        promptName: 'Clasificacion de noticias',
        module: 'classification',
        version: '1.0.0',
        checksum: 'checksum',
        active: true,
        createdAt: '2026-06-18T10:00:00Z'
      },
      {
        promptKey: 'WF04_ANALYSIS',
        promptName: 'Analisis de eventos',
        module: 'analysis',
        version: '1.1.0',
        checksum: 'checksum-analysis',
        active: false,
        createdAt: '2026-06-18T11:00:00Z'
      }
    ];
  }

  function metrics() {
    return {
      totalOperations: 2,
      successCount: 1,
      failedCount: 1,
      averageLatencyMs: 170,
      recentMetrics: [
        {
          id: 1,
          operationType: 'CLASSIFICATION',
          promptKey: 'WF02_CLASSIFICATION',
          provider: 'GeminiAIProvider',
          model: 'gemini-1.5-flash',
          status: 'SUCCESS' as const,
          relatedEntityType: 'NEWS',
          relatedEntityId: 1,
          latencyMs: 120,
          errorMessage: null,
          createdAt: '2026-06-18T10:00:00Z'
        },
        {
          id: 2,
          operationType: 'ANALYSIS',
          promptKey: 'WF04_ANALYSIS',
          provider: 'GeminiAIProvider',
          model: 'gemini-1.5-flash',
          status: 'FAILED' as const,
          relatedEntityType: 'EVENT',
          relatedEntityId: 2,
          latencyMs: 220,
          errorMessage: 'Respuesta IA invalida',
          createdAt: '2026-06-18T11:00:00Z'
        }
      ]
      ,
      p95LatencyMs: 220,
      successRate: 50,
      failureRate: 50,
      previousTotalOperations: 1,
      previousSuccessCount: 1,
      previousFailedCount: 0,
      previousAverageLatencyMs: 100,
      totalDifference: 1,
      successRateDifference: -50,
      failureRateDifference: 50,
      averageLatencyDifference: 70
    };
  }
});
