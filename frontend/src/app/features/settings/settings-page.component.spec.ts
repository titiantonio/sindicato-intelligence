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
    aiObservabilityService = jasmine.createSpyObj<AiObservabilityService>('AiObservabilityService', [
      'listPrompts',
      'listMetrics',
      'listDailyMetrics',
      'listProviders',
      'updateProvider',
      'listProviderModels',
      'listWorkflowSettings',
      'updateWorkflowSetting'
    ]);
    applicationSettingsService = jasmine.createSpyObj<ApplicationSettingsService>('ApplicationSettingsService', ['getTelegramSettings', 'updateTelegramSettings']);
    automationService = jasmine.createSpyObj<AutomationService>('AutomationService', ['listSettings', 'updateSetting', 'runWorkflow', 'getOverview', 'listOperations']);
    aiObservabilityService.listPrompts.and.returnValue(of(promptVersions()));
    aiObservabilityService.listMetrics.and.returnValue(of(metrics()));
    aiObservabilityService.listDailyMetrics.and.returnValue(of(metrics()));
    aiObservabilityService.listProviders.and.returnValue(of(aiProviders()));
    aiObservabilityService.updateProvider.and.returnValue(of(aiProviders()[1]));
    aiObservabilityService.listProviderModels.and.returnValue(of([{ name: 'models/gemini-2.5-flash', displayName: 'Gemini 2.5 Flash' }]));
    aiObservabilityService.listWorkflowSettings.and.returnValue(of(aiWorkflowSettings()));
    aiObservabilityService.updateWorkflowSetting.and.returnValue(of(aiWorkflowSettings()[0]));
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
    automationService.listOperations.and.returnValue(of(operations()));

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

  it('renders ai metrics tab by default', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Metricas IA');
    expect(compiled.textContent).toContain('Metricas diarias');
    expect(compiled.textContent).toContain('Operaciones del dia');
    expect(compiled.textContent).toContain('WF02_CLASSIFICATION');
    expect(compiled.textContent).toContain('GeminiAIProvider');
    expect(compiled.textContent).toContain('gemini-1.5-flash');
    expect(compiled.textContent).not.toContain('Prompts versionados');
    expect(compiled.textContent).not.toContain('Proveedores IA');
    expect(compiled.textContent).not.toContain('Guardar Telegram');
    expect(aiObservabilityService.listDailyMetrics).toHaveBeenCalled();
  });

  it('renders metric table without redundant workflow and operation columns', () => {
    const compiled = fixture.nativeElement as HTMLElement;
    const headers = Array.from(compiled.querySelectorAll('.metric-operations-table thead tr:first-child th'))
      .map((header) => header.textContent?.replace(/\s+/g, ' ').trim());

    expect(headers).toEqual(['Fecha DESC', 'Prompt', 'Estado', 'Proveedor', 'Modelo', 'Entidad', 'Latencia', 'Error']);
    expect(headers).not.toContain('WF');
    expect(headers).not.toContain('Operacion');
  });

  it('renders long model names in a wrapping metric table cell', () => {
    automationService.listOperations.and.returnValue(of([
      {
        ...operations()[0],
        model: 'models/gemma-4-31b-it:conservative-recitation-fallback'
      }
    ]));

    const localFixture = TestBed.createComponent(SettingsPageComponent);
    localFixture.detectChanges();
    const compiled = localFixture.nativeElement as HTMLElement;
    const modelCell = compiled.querySelector('.metric-model-cell');

    expect(modelCell?.textContent).toContain('models/gemma-4-31b-it:conservative-recitation-fallback');
    expect(modelCell?.classList).toContain('table-cell-break');
  });

  it('renders prompts, publication and automation configuration in separate tabs', () => {
    const component = fixture.componentInstance as any;
    const compiled = fixture.nativeElement as HTMLElement;

    component.setTab('prompts');
    fixture.detectChanges();

    expect(compiled.textContent).toContain('Prompts versionados');
    expect(compiled.textContent).toContain('Clasificacion de noticias');
    expect(compiled.textContent).not.toContain('Credenciales y modelos');
    expect(compiled.textContent).not.toContain('Guardar Telegram');

    component.setTab('publication');
    fixture.detectChanges();

    expect(compiled.textContent).toContain('Telegram');
    expect(compiled.textContent).toContain('Guardar Telegram');
    expect(compiled.textContent).not.toContain('WF02 - Clasificacion');

    component.setTab('automation');
    fixture.detectChanges();

    expect(compiled.textContent).toContain('WF02 - Clasificacion');
    expect(compiled.textContent).toContain('Credenciales y modelos');
    expect(compiled.textContent).toContain('Guardar IA');
    expect(compiled.textContent).toContain('Cooldown modelo (seg)');
    expect(compiled.textContent).toContain('Google Gemini');
    expect(compiled.textContent).toContain('Procesadas: 0 · Completadas: 0 · Fallidas: 0 · Omitidas: 0');
    expect(compiled.textContent).not.toContain('P/C/F/O');
    expect(component.formFor('WF02_CLASSIFICATION').intervalMinutes).toBe(10);
  });

  it('saves edited settings in seconds', () => {
    (fixture.componentInstance as any).setTab('automation');
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
      disableWebPagePreview: true,
      maxAttachmentCount: 10,
      maxAttachmentFileBytes: 20971520,
      maxAttachmentTotalBytes: 52428800
    });
  });

  it('loads models and saves ai provider settings', () => {
    const component = fixture.componentInstance as any;

    component.updateAiProviderForm('gemini', { enabled: true, apiKey: 'test-key' });
    component.loadModels('gemini');
    component.saveAiProvider(aiProviders()[1]);

    expect(aiObservabilityService.listProviderModels).toHaveBeenCalledWith('gemini', 'test-key');
    expect(aiObservabilityService.updateProvider).toHaveBeenCalledWith('gemini', {
      enabled: true,
      apiKey: 'test-key'
    });
  });

  it('loads workflow models when the model selector is opened', () => {
    const component = fixture.componentInstance as any;

    component.setTab('automation');
    component.updateAiWorkflowForm('WF04_ANALYSIS', { providerCode: 'gemini' });
    component.loadWorkflowModels('WF04_ANALYSIS');
    component.loadWorkflowModels('WF04_ANALYSIS');

    expect(aiObservabilityService.listProviderModels).toHaveBeenCalledOnceWith('gemini', null);
  });

  it('clears ai provider API after confirmation', () => {
    const component = fixture.componentInstance as any;

    component.setTab('automation');
    component.requestClearAiProviderApiKey(aiProviders()[1]);
    fixture.detectChanges();

    expect(component.pendingSecretDeletion()?.title).toBe('Eliminar API de IA');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Eliminar API de IA');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Cancelar');
    expect(aiObservabilityService.updateProvider).not.toHaveBeenCalledWith('gemini', jasmine.objectContaining({ clearApiKey: true }));

    component.confirmSecretDeletion();

    expect(aiObservabilityService.updateProvider).toHaveBeenCalledWith('gemini', {
      enabled: false,
      apiKey: null,
      clearApiKey: true
    });
  });

  it('clears Telegram token using the frontend confirmation dialog', () => {
    const component = fixture.componentInstance as any;
    applicationSettingsService.getTelegramSettings.and.returnValue(of({
      ...telegramSettings(),
      botTokenConfigured: true,
      botTokenPreview: '1234...oken',
      chatId: 'chat-id'
    }));
    applicationSettingsService.updateTelegramSettings.and.returnValue(of({
      ...telegramSettings(),
      botTokenConfigured: false,
      botTokenPreview: null,
      chatId: 'chat-id'
    }));

    component.setTab('publication');
    component.requestClearTelegramBotToken();
    fixture.detectChanges();

    expect(component.pendingSecretDeletion()?.title).toBe('Eliminar token del bot de Telegram');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Eliminar token');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Cancelar');
    expect(applicationSettingsService.updateTelegramSettings).not.toHaveBeenCalledWith(jasmine.objectContaining({ clearBotToken: true }));

    component.confirmSecretDeletion();

    expect(applicationSettingsService.updateTelegramSettings).toHaveBeenCalledWith({
      enabled: false,
      baseUrl: 'https://api.telegram.org',
      botToken: null,
      clearBotToken: true,
      chatId: 'chat-id',
      disableWebPagePreview: true,
      maxAttachmentCount: 10,
      maxAttachmentFileBytes: 20971520,
      maxAttachmentTotalBytes: 52428800,
      destinations: [{ id: null, name: 'Principal', chatId: 'chat-id', active: true, defaultSelected: true }]
    });
  });

  it('saves ai workflow provider and model settings', () => {
    const component = fixture.componentInstance as any;

    component.updateAiWorkflowForm('WF04_ANALYSIS', {
      providerCode: 'gemini',
      modelName: 'models/gemini-2.5-flash',
      temperature: 0.3,
      maxOutputTokens: 2048,
      cooldownSeconds: 120
    });
    component.saveAiWorkflow(aiWorkflowSettings()[2]);

    expect(aiObservabilityService.updateWorkflowSetting).toHaveBeenCalledWith('WF04_ANALYSIS', {
      providerCode: 'gemini',
      modelName: 'models/gemini-2.5-flash',
      temperature: 0.3,
      maxOutputTokens: 2048,
      cooldownSeconds: 120
    });
  });

  it('filters, sorts and paginates prompt versions', () => {
    const component = fixture.componentInstance as any;

    component.setTab('prompts');
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
    expect(component.displayedMetrics().length).toBe(1);
    expect(component.displayedMetrics()[0].status).toBe('FAILED');

    component.setMetricStatusFilter('');
    component.changeMetricSort('latencyMs');
    expect(component.displayedMetrics()[0].latencyMs).toBe(null);

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
    expect(component.aiMetricCards()[1].title).toBe('Calidad');
    expect(component.aiMetricCards()[1].items[2].value).toBe(50);
    expect(component.aiMetricCards()[2].title).toBe('Errores');
    expect(component.aiMetricCards()[2].tone).toBe('danger');
  });

  it('changes daily metrics date', () => {
    const component = fixture.componentInstance as any;

    component.setMetricDate('2026-06-17');

    expect(aiObservabilityService.listDailyMetrics).toHaveBeenCalledWith('2026-06-17');
    expect(automationService.listOperations).toHaveBeenCalledWith('2026-06-17');
  });

  it('opens error and detail modals from metric rows', () => {
    const component = fixture.componentInstance as any;
    const failedMetric = operations()[1];

    component.openMetricDetail(failedMetric);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Detalle de publicacion Telegram');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Abrir evento relacionado');
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Publicacion');

    component.closeMetricDetail();
    component.openMetricError(new Event('click'), failedMetric);
    fixture.detectChanges();
    expect((fixture.nativeElement as HTMLElement).textContent).toContain('Telegram publication failed');
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
      maxAttachmentCount: 10,
      maxAttachmentFileBytes: 20971520,
      maxAttachmentTotalBytes: 52428800,
      updatedAt: '2026-06-16T10:00:00Z'
    };
  }

  function aiProviders() {
    return [
      {
        providerCode: 'deterministic',
        displayName: 'Determinista local',
        enabled: true,
        apiKeyConfigured: false,
        apiKeyPreview: null,
        createdAt: '2026-06-24T10:00:00Z',
        updatedAt: '2026-06-24T10:00:00Z'
      },
      {
        providerCode: 'gemini',
        displayName: 'Google Gemini',
        enabled: false,
        apiKeyConfigured: true,
        apiKeyPreview: 'abcd...wxyz',
        createdAt: '2026-06-24T10:00:00Z',
        updatedAt: '2026-06-24T10:00:00Z'
      }
    ];
  }

  function aiWorkflowSettings() {
    return [
      {
        workflowCode: 'WF02_CLASSIFICATION',
        providerCode: 'deterministic',
        providerName: 'Determinista local',
        modelName: 'deterministic-classification',
        temperature: 0.2,
        maxOutputTokens: 1024,
        cooldownSeconds: 60,
        createdAt: '2026-06-24T10:00:00Z',
        updatedAt: '2026-06-24T10:00:00Z'
      },
      {
        workflowCode: 'WF03_EVENT_MATCHING',
        providerCode: 'deterministic',
        providerName: 'Determinista local',
        modelName: 'deterministic-event-matching',
        temperature: 0.2,
        maxOutputTokens: 1024,
        cooldownSeconds: 60,
        createdAt: '2026-06-24T10:00:00Z',
        updatedAt: '2026-06-24T10:00:00Z'
      },
      {
        workflowCode: 'WF04_ANALYSIS',
        providerCode: 'deterministic',
        providerName: 'Determinista local',
        modelName: 'deterministic-analysis',
        temperature: 0.2,
        maxOutputTokens: 1024,
        cooldownSeconds: 60,
        createdAt: '2026-06-24T10:00:00Z',
        updatedAt: '2026-06-24T10:00:00Z'
      },
      {
        workflowCode: 'WF05_CONTENT',
        providerCode: 'deterministic',
        providerName: 'Determinista local',
        modelName: 'deterministic-content',
        temperature: 0.2,
        maxOutputTokens: 1024,
        cooldownSeconds: 60,
        createdAt: '2026-06-24T10:00:00Z',
        updatedAt: '2026-06-24T10:00:00Z'
      }
    ];
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

  function operations() {
    return [
      {
        id: 'AI-1',
        workflowCode: 'WF02_CLASSIFICATION',
        operationType: 'CLASSIFICATION',
        promptKey: 'WF02_CLASSIFICATION',
        provider: 'GeminiAIProvider',
        model: 'gemini-1.5-flash',
        status: 'SUCCESS' as const,
        relatedEntityType: 'NEWS',
        relatedEntityId: 1,
        latencyMs: 120,
        errorMessage: null,
        createdAt: '2026-06-18T10:00:00Z',
        details: {
          category: 'OTROS',
          subcategory: 'FUERA_DE_AMBITO',
          relevance: 0,
          finalNewsStatus: 'DISCARDED',
          discardReason: 'FUERA_DE_AMBITO'
        }
      },
      {
        id: 'WF06-2',
        workflowCode: 'WF06_PUBLICATION_TELEGRAM',
        operationType: 'TELEGRAM_PUBLICATION',
        promptKey: null,
        provider: 'Telegram',
        model: null,
        status: 'FAILED' as const,
        relatedEntityType: 'PUBLICATION',
        relatedEntityId: 2,
        latencyMs: null,
        errorMessage: 'Telegram publication failed',
        createdAt: '2026-06-18T11:00:00Z',
        details: {
          publicationId: 2,
          contentId: 3,
          eventId: 4,
          channel: 'TELEGRAM',
          publicationStatus: 'FAILED',
          triggerType: 'IMMEDIATE',
          error: 'Telegram publication failed'
        }
      }
    ];
  }
});
