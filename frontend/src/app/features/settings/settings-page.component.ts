import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { AiMetricsSnapshot, AiModelOption, AiPromptVersion, AiProviderSetting, AiWorkflowSetting } from '../../core/models/ai-observability.models';
import { TelegramPublicationSettings } from '../../core/models/application-settings.models';
import { AutomationOverview, AutomationRunResult, AutomationWorkflowCode, AutomationWorkflowSetting, WorkflowOperation } from '../../core/models/automation.models';
import { MetricCard } from '../../core/models/dashboard.models';
import { AiObservabilityService } from '../../core/services/ai-observability.service';
import { ApplicationSettingsService } from '../../core/services/application-settings.service';
import { AutomationService } from '../../core/services/automation.service';
import { MetricCardComponent } from '../../shared/components/metric-card/metric-card.component';

type SettingsTab = 'metrics' | 'prompts' | 'automation' | 'publication';
type SortDirection = 'asc' | 'desc';
type PromptSortColumn = 'promptKey' | 'promptName' | 'module' | 'version' | 'checksum' | 'active' | 'createdAt';
type MetricSortColumn = 'workflowCode' | 'operationType' | 'promptKey' | 'provider' | 'model' | 'status' | 'relatedEntityType' | 'latencyMs' | 'errorMessage' | 'createdAt';

interface AutomationSettingForm {
  enabled: boolean;
  intervalMinutes: number;
  batchSize: number;
}

interface TelegramSettingsForm {
  enabled: boolean;
  baseUrl: string;
  botToken: string;
  chatId: string;
  disableWebPagePreview: boolean;
}

interface AiProviderForm {
  enabled: boolean;
  apiKey: string;
}

interface AiWorkflowForm {
  providerCode: string;
  modelName: string;
  temperature: number;
  maxOutputTokens: number;
}

@Component({
  selector: 'app-settings-page',
  imports: [FormsModule, MetricCardComponent, RouterLink],
  templateUrl: './settings-page.component.html',
  styleUrl: './settings-page.component.scss'
})
export class SettingsPageComponent implements OnInit {
  private readonly automationService = inject(AutomationService);
  private readonly applicationSettingsService = inject(ApplicationSettingsService);
  private readonly aiObservabilityService = inject(AiObservabilityService);

  protected readonly settings = signal<AutomationWorkflowSetting[]>([]);
  protected readonly overview = signal<AutomationOverview | null>(null);
  protected readonly promptVersions = signal<AiPromptVersion[]>([]);
  protected readonly aiProviders = signal<AiProviderSetting[]>([]);
  protected readonly aiWorkflowSettings = signal<AiWorkflowSetting[]>([]);
  protected readonly aiProviderForms = signal<Record<string, AiProviderForm>>({});
  protected readonly aiWorkflowForms = signal<Record<string, AiWorkflowForm>>({});
  protected readonly aiModelOptions = signal<Record<string, AiModelOption[]>>({});
  protected readonly aiMetrics = signal<AiMetricsSnapshot | null>(null);
  protected readonly workflowOperations = signal<WorkflowOperation[]>([]);
  protected readonly forms = signal<Record<string, AutomationSettingForm>>({});
  protected readonly telegramSettings = signal<TelegramPublicationSettings | null>(null);
  protected readonly telegramForm = signal<TelegramSettingsForm>({
    enabled: false,
    baseUrl: 'https://api.telegram.org',
    botToken: '',
    chatId: '',
    disableWebPagePreview: true
  });
  protected readonly isLoading = signal(false);
  protected readonly isTelegramLoading = signal(false);
  protected readonly isAiLoading = signal(false);
  protected readonly isTelegramSaving = signal(false);
  protected readonly busyAiProvider = signal<string | null>(null);
  protected readonly busyAiWorkflow = signal<string | null>(null);
  protected readonly busyModelProvider = signal<string | null>(null);
  protected readonly busyWorkflow = signal<AutomationWorkflowCode | null>(null);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly lastRunResult = signal<Record<string, AutomationRunResult>>({});
  protected readonly activeTab = signal<SettingsTab>('metrics');
  protected readonly pageSizeOptions = [5, 10, 25, 50];
  protected readonly metricDate = signal(this.todayInputValue());
  protected readonly selectedErrorMetric = signal<WorkflowOperation | null>(null);
  protected readonly selectedMetricDetail = signal<WorkflowOperation | null>(null);

  protected readonly promptKeyFilter = signal('');
  protected readonly promptNameFilter = signal('');
  protected readonly promptModuleFilter = signal('');
  protected readonly promptVersionFilter = signal('');
  protected readonly promptChecksumFilter = signal('');
  protected readonly promptActiveFilter = signal('');
  protected readonly promptCreatedAtFilter = signal('');
  protected readonly promptSortColumn = signal<PromptSortColumn>('promptKey');
  protected readonly promptSortDirection = signal<SortDirection>('asc');
  protected readonly promptPageSize = signal(10);
  protected readonly promptCurrentPage = signal(1);
  protected readonly displayedPrompts = computed(() => this.sortPrompts(this.filterPrompts(this.promptVersions())));
  protected readonly promptTotalPages = computed(() => Math.max(1, Math.ceil(this.displayedPrompts().length / this.promptPageSize())));
  protected readonly promptDisplayPage = computed(() => Math.min(this.promptCurrentPage(), this.promptTotalPages()));
  protected readonly paginatedPrompts = computed(() => {
    const page = this.promptDisplayPage();
    const start = (page - 1) * this.promptPageSize();
    return this.displayedPrompts().slice(start, start + this.promptPageSize());
  });

  protected readonly metricOperationFilter = signal('');
  protected readonly metricPromptFilter = signal('');
  protected readonly metricProviderFilter = signal('');
  protected readonly metricModelFilter = signal('');
  protected readonly metricStatusFilter = signal('');
  protected readonly metricRelatedEntityTypeFilter = signal('');
  protected readonly metricLatencyFilter = signal('');
  protected readonly metricErrorFilter = signal('');
  protected readonly metricCreatedAtFilter = signal('');
  protected readonly metricSortColumn = signal<MetricSortColumn>('createdAt');
  protected readonly metricSortDirection = signal<SortDirection>('desc');
  protected readonly metricPageSize = signal(10);
  protected readonly metricCurrentPage = signal(1);
  protected readonly displayedMetrics = computed(() => this.sortMetrics(this.filterMetrics(this.workflowOperations())));
  protected readonly metricTotalPages = computed(() => Math.max(1, Math.ceil(this.displayedMetrics().length / this.metricPageSize())));
  protected readonly metricDisplayPage = computed(() => Math.min(this.metricCurrentPage(), this.metricTotalPages()));
  protected readonly paginatedMetrics = computed(() => {
    const page = this.metricDisplayPage();
    const start = (page - 1) * this.metricPageSize();
    return this.displayedMetrics().slice(start, start + this.metricPageSize());
  });
  protected readonly aiMetricCards = computed(() => this.toAiMetricCards(this.aiMetrics()));

  ngOnInit(): void {
    this.loadSettings();
    this.loadTelegramSettings();
    this.loadAiObservability();
  }

  protected loadSettings(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.automationService.listSettings().subscribe({
      next: (settings) => {
        this.settings.set(settings);
        this.forms.set(Object.fromEntries(settings.map((setting) => [setting.workflowCode, this.toForm(setting)])));
        this.isLoading.set(false);
        this.loadOverview();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la configuracion de automatizaciones.');
        this.isLoading.set(false);
      }
    });
  }

  protected loadOverview(): void {
    this.automationService.getOverview().subscribe({
      next: (overview) => {
        this.overview.set(overview);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la vision operativa de automatizaciones.');
      }
    });
  }

  protected loadTelegramSettings(): void {
    this.isTelegramLoading.set(true);
    this.errorMessage.set(null);

    this.applicationSettingsService.getTelegramSettings().subscribe({
      next: (settings) => {
        this.telegramSettings.set(settings);
        this.telegramForm.set({
          enabled: settings.enabled,
          baseUrl: settings.baseUrl,
          botToken: '',
          chatId: settings.chatId ?? '',
          disableWebPagePreview: settings.disableWebPagePreview
        });
        this.isTelegramLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la configuracion de Telegram.');
        this.isTelegramLoading.set(false);
      }
    });
  }

  protected loadAiObservability(): void {
    this.isAiLoading.set(true);
    this.aiObservabilityService.listPrompts().subscribe({
      next: (prompts) => {
        this.promptVersions.set(prompts);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar el versionado de prompts IA.');
      }
    });
    this.loadAiProviderSettings();
    this.aiObservabilityService.listDailyMetrics(this.metricDate()).subscribe({
      next: (metrics) => {
        this.aiMetrics.set(metrics);
        this.loadWorkflowOperations();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudieron cargar las metricas IA.');
        this.isAiLoading.set(false);
      }
    });
  }

  protected loadWorkflowOperations(): void {
    this.automationService.listOperations(this.metricDate()).subscribe({
      next: (operations) => {
        this.workflowOperations.set(operations);
        this.isAiLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudieron cargar las operaciones del dia.');
        this.isAiLoading.set(false);
      }
    });
  }

  protected loadAiProviderSettings(): void {
    this.aiObservabilityService.listProviders().subscribe({
      next: (providers) => {
        this.aiProviders.set(providers);
        this.aiProviderForms.set(Object.fromEntries(providers.map((provider) => [
          provider.providerCode,
          { enabled: provider.enabled, apiKey: '' }
        ])));
        this.loadAiWorkflowSettings();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la configuracion de proveedores IA.');
      }
    });
  }

  protected loadAiWorkflowSettings(): void {
    this.aiObservabilityService.listWorkflowSettings().subscribe({
      next: (settings) => {
        this.aiWorkflowSettings.set(settings);
        this.aiWorkflowForms.set(Object.fromEntries(settings.map((setting) => [
          setting.workflowCode,
          {
            providerCode: setting.providerCode,
            modelName: setting.modelName,
            temperature: setting.temperature,
            maxOutputTokens: setting.maxOutputTokens
          }
        ])));
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la configuracion IA por workflow.');
      }
    });
  }

  protected setTab(tab: SettingsTab): void {
    this.activeTab.set(tab);
  }

  protected updateEnabled(workflowCode: AutomationWorkflowCode, value: boolean): void {
    this.updateForm(workflowCode, { enabled: value });
  }

  protected updateInterval(workflowCode: AutomationWorkflowCode, value: string | number): void {
    this.updateForm(workflowCode, { intervalMinutes: Math.max(1, Number(value)) });
  }

  protected updateBatchSize(workflowCode: AutomationWorkflowCode, value: string | number): void {
    this.updateForm(workflowCode, { batchSize: Math.max(1, Number(value)) });
  }

  protected updateTelegramForm(patch: Partial<TelegramSettingsForm>): void {
    this.telegramForm.update((form) => ({ ...form, ...patch }));
  }

  protected updateAiProviderForm(providerCode: string, patch: Partial<AiProviderForm>): void {
    this.aiProviderForms.update((forms) => ({
      ...forms,
      [providerCode]: { ...this.aiProviderFormFor(providerCode), ...patch }
    }));
  }

  protected updateAiWorkflowForm(workflowCode: string, patch: Partial<AiWorkflowForm>): void {
    this.aiWorkflowForms.update((forms) => ({
      ...forms,
      [workflowCode]: { ...this.aiWorkflowFormFor(workflowCode), ...patch }
    }));
  }

  protected aiProviderFormFor(providerCode: string): AiProviderForm {
    return this.aiProviderForms()[providerCode] ?? { enabled: false, apiKey: '' };
  }

  protected aiWorkflowFormFor(workflowCode: string): AiWorkflowForm {
    return this.aiWorkflowForms()[workflowCode] ?? { providerCode: 'deterministic', modelName: '', temperature: 0.2, maxOutputTokens: 1024 };
  }

  protected saveAiProvider(provider: AiProviderSetting): void {
    const form = this.aiProviderFormFor(provider.providerCode);
    this.busyAiProvider.set(provider.providerCode);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.aiObservabilityService.updateProvider(provider.providerCode, {
      enabled: form.enabled,
      apiKey: form.apiKey.trim() ? form.apiKey.trim() : null
    }).subscribe({
      next: () => {
        this.successMessage.set('Proveedor IA guardado.');
        this.busyAiProvider.set(null);
        this.loadAiProviderSettings();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo guardar el proveedor IA.');
        this.busyAiProvider.set(null);
      }
    });
  }

  protected loadModels(providerCode: string): void {
    const form = this.aiProviderFormFor(providerCode);
    this.busyModelProvider.set(providerCode);
    this.errorMessage.set(null);
    this.aiObservabilityService.listProviderModels(providerCode, form.apiKey.trim() ? form.apiKey.trim() : null).subscribe({
      next: (models) => {
        this.aiModelOptions.update((options) => ({ ...options, [providerCode]: models }));
        this.busyModelProvider.set(null);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudieron cargar los modelos del proveedor IA.');
        this.busyModelProvider.set(null);
      }
    });
  }

  protected saveAiWorkflow(setting: AiWorkflowSetting): void {
    const form = this.aiWorkflowFormFor(setting.workflowCode);
    this.busyAiWorkflow.set(setting.workflowCode);
    this.errorMessage.set(null);
    this.successMessage.set(null);
    this.aiObservabilityService.updateWorkflowSetting(setting.workflowCode, {
      providerCode: form.providerCode,
      modelName: form.modelName,
      temperature: Number(form.temperature),
      maxOutputTokens: Math.max(1, Math.round(Number(form.maxOutputTokens)))
    }).subscribe({
      next: () => {
        this.successMessage.set('Configuracion IA del workflow guardada.');
        this.busyAiWorkflow.set(null);
        this.loadAiWorkflowSettings();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo guardar la configuracion IA del workflow.');
        this.busyAiWorkflow.set(null);
      }
    });
  }

  protected saveTelegramSettings(): void {
    const form = this.telegramForm();
    this.isTelegramSaving.set(true);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.applicationSettingsService.updateTelegramSettings({
      enabled: form.enabled,
      baseUrl: form.baseUrl,
      botToken: form.botToken.trim() ? form.botToken.trim() : null,
      chatId: form.chatId.trim() ? form.chatId.trim() : null,
      disableWebPagePreview: form.disableWebPagePreview
    }).subscribe({
      next: (settings) => {
        this.telegramSettings.set(settings);
        this.telegramForm.set({
          enabled: settings.enabled,
          baseUrl: settings.baseUrl,
          botToken: '',
          chatId: settings.chatId ?? '',
          disableWebPagePreview: settings.disableWebPagePreview
        });
        this.successMessage.set(settings.readyToPublish ? 'Configuracion de Telegram guardada y lista para publicar.' : 'Configuracion de Telegram guardada.');
        this.isTelegramSaving.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo guardar la configuracion de Telegram.');
        this.isTelegramSaving.set(false);
      }
    });
  }

  protected save(setting: AutomationWorkflowSetting): void {
    const form = this.forms()[setting.workflowCode];
    if (!form) {
      return;
    }

    this.busyWorkflow.set(setting.workflowCode);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.automationService.updateSetting(setting.workflowCode, {
      enabled: form.enabled,
      intervalSeconds: Math.max(60, Math.round(form.intervalMinutes * 60)),
      batchSize: Math.max(1, Math.round(form.batchSize))
    }).subscribe({
      next: () => {
        this.successMessage.set('Configuracion guardada.');
        this.busyWorkflow.set(null);
        this.loadSettings();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo guardar la configuracion.');
        this.busyWorkflow.set(null);
      }
    });
  }

  protected runNow(setting: AutomationWorkflowSetting): void {
    this.busyWorkflow.set(setting.workflowCode);
    this.errorMessage.set(null);
    this.successMessage.set(null);

    this.automationService.runWorkflow(setting.workflowCode).subscribe({
      next: (result) => {
        this.lastRunResult.update((value) => ({ ...value, [setting.workflowCode]: result }));
        this.successMessage.set(`Ejecucion finalizada. Procesados ${result.processedCount}. Correctos ${result.successCount}. Fallidos ${result.failedCount}.`);
        this.busyWorkflow.set(null);
        this.loadSettings();
        this.loadAiObservability();
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo ejecutar la automatizacion.');
        this.busyWorkflow.set(null);
      }
    });
  }

  protected formFor(workflowCode: AutomationWorkflowCode): AutomationSettingForm {
    return this.forms()[workflowCode] ?? { enabled: false, intervalMinutes: 10, batchSize: 1 };
  }

  protected workflowLabel(workflowCode: AutomationWorkflowCode): string {
    const labels: Record<AutomationWorkflowCode, string> = {
      WF02_CLASSIFICATION: 'WF02 - Clasificacion',
      WF03_EVENT_DETECTION: 'WF03 - Deteccion de eventos',
      WF04_ANALYSIS: 'WF04 - Analisis'
    };
    return labels[workflowCode];
  }

  protected aiWorkflowLabel(workflowCode: string): string {
    const labels: Record<string, string> = {
      WF02_CLASSIFICATION: 'WF02 - Clasificacion',
      WF03_EVENT_MATCHING: 'WF03 - Matching de eventos',
      WF04_ANALYSIS: 'WF04 - Analisis',
      WF05_CONTENT: 'WF05 - Generacion de contenido'
    };
    return labels[workflowCode] ?? workflowCode;
  }

  protected automationSettingForAiWorkflow(workflowCode: string): AutomationWorkflowSetting | null {
    const automationCode = this.toAutomationWorkflowCode(workflowCode);
    if (!automationCode) {
      return null;
    }
    return this.settings().find((setting) => setting.workflowCode === automationCode) ?? null;
  }

  protected modelsFor(providerCode: string): AiModelOption[] {
    return this.aiModelOptions()[providerCode] || [];
  }

  protected formatDate(value: string | null): string {
    if (!value) {
      return 'Sin registro';
    }
    return new Intl.DateTimeFormat('es-ES', {
      dateStyle: 'short',
      timeStyle: 'short'
    }).format(new Date(value));
  }

  protected resultText(setting: AutomationWorkflowSetting): string {
    const liveResult = this.lastRunResult()[setting.workflowCode];
    if (liveResult) {
      return this.formatRunCounts(
        liveResult.processedCount,
        liveResult.successCount,
        liveResult.failedCount,
        liveResult.skippedCount
      );
    }
    return this.formatRunCounts(
      setting.lastProcessedCount,
      setting.lastSuccessCount,
      setting.lastFailedCount,
      setting.lastSkippedCount
    );
  }

  private formatRunCounts(processed: number, completed: number, failed: number, skipped: number): string {
    return `Procesadas: ${processed} · Completadas: ${completed} · Fallidas: ${failed} · Omitidas: ${skipped}`;
  }

  protected metricStatusLabel(status: string): string {
    return status === 'SUCCESS' ? 'Correcta' : 'Fallida';
  }

  protected promptSortLabel(column: PromptSortColumn): string {
    return this.promptSortColumn() === column ? (this.promptSortDirection() === 'asc' ? 'ASC' : 'DESC') : '';
  }

  protected metricSortLabel(column: MetricSortColumn): string {
    return this.metricSortColumn() === column ? (this.metricSortDirection() === 'asc' ? 'ASC' : 'DESC') : '';
  }

  protected changePromptSort(column: PromptSortColumn): void {
    if (this.promptSortColumn() === column) {
      this.promptSortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }
    this.promptSortColumn.set(column);
    this.promptSortDirection.set(column === 'createdAt' ? 'desc' : 'asc');
  }

  protected changeMetricSort(column: MetricSortColumn): void {
    if (this.metricSortColumn() === column) {
      this.metricSortDirection.update((direction) => direction === 'asc' ? 'desc' : 'asc');
      return;
    }
    this.metricSortColumn.set(column);
    this.metricSortDirection.set(column === 'createdAt' ? 'desc' : 'asc');
  }

  protected setPromptKeyFilter(value: string): void { this.promptKeyFilter.set(value); this.promptCurrentPage.set(1); }
  protected setPromptNameFilter(value: string): void { this.promptNameFilter.set(value); this.promptCurrentPage.set(1); }
  protected setPromptModuleFilter(value: string): void { this.promptModuleFilter.set(value); this.promptCurrentPage.set(1); }
  protected setPromptVersionFilter(value: string): void { this.promptVersionFilter.set(value); this.promptCurrentPage.set(1); }
  protected setPromptChecksumFilter(value: string): void { this.promptChecksumFilter.set(value); this.promptCurrentPage.set(1); }
  protected setPromptActiveFilter(value: string): void { this.promptActiveFilter.set(value); this.promptCurrentPage.set(1); }
  protected setPromptCreatedAtFilter(value: string): void { this.promptCreatedAtFilter.set(value); this.promptCurrentPage.set(1); }
  protected setPromptPageSize(value: string): void { this.promptPageSize.set(Number(value)); this.promptCurrentPage.set(1); }
  protected previousPromptPage(): void { this.promptCurrentPage.update((page) => Math.max(1, page - 1)); }
  protected nextPromptPage(): void { this.promptCurrentPage.update((page) => Math.min(this.promptTotalPages(), page + 1)); }

  protected setMetricOperationFilter(value: string): void { this.metricOperationFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricPromptFilter(value: string): void { this.metricPromptFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricProviderFilter(value: string): void { this.metricProviderFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricModelFilter(value: string): void { this.metricModelFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricStatusFilter(value: string): void { this.metricStatusFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricRelatedEntityTypeFilter(value: string): void { this.metricRelatedEntityTypeFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricLatencyFilter(value: string): void { this.metricLatencyFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricErrorFilter(value: string): void { this.metricErrorFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricCreatedAtFilter(value: string): void { this.metricCreatedAtFilter.set(value); this.metricCurrentPage.set(1); }
  protected setMetricPageSize(value: string): void { this.metricPageSize.set(Number(value)); this.metricCurrentPage.set(1); }
  protected previousMetricPage(): void { this.metricCurrentPage.update((page) => Math.max(1, page - 1)); }
  protected nextMetricPage(): void { this.metricCurrentPage.update((page) => Math.min(this.metricTotalPages(), page + 1)); }
  protected setMetricDate(value: string): void {
    this.metricDate.set(value || this.todayInputValue());
    this.metricCurrentPage.set(1);
    this.loadAiObservability();
  }

  protected openMetricError(event: Event, metric: WorkflowOperation): void {
    event.stopPropagation();
    this.selectedErrorMetric.set(metric);
  }

  protected closeMetricError(): void {
    this.selectedErrorMetric.set(null);
  }

  protected openMetricDetail(metric: WorkflowOperation): void {
    this.selectedMetricDetail.set(metric);
  }

  protected closeMetricDetail(): void {
    this.selectedMetricDetail.set(null);
  }

  protected metricDetailTitle(metric: WorkflowOperation): string {
    const labels: Record<string, string> = {
      CLASSIFICATION: 'Detalle de clasificacion IA',
      EVENT_MATCHING: 'Detalle de matching de evento',
      ANALYSIS: 'Detalle de analisis IA',
      CONTENT_GENERATION: 'Detalle de generacion de contenido',
      TELEGRAM_PUBLICATION: 'Detalle de publicacion Telegram'
    };
    return labels[metric.operationType] ?? 'Detalle de operacion IA';
  }

  protected entityDetailLink(metric: WorkflowOperation): string[] | null {
    if (metric.relatedEntityType === 'EVENT' && metric.relatedEntityId) {
      return ['/events', metric.relatedEntityId.toString()];
    }
    const eventId = this.detailNumber(metric, 'eventId');
    if (eventId) {
      return ['/events', eventId.toString()];
    }
    return null;
  }

  protected workflowLabelForOperation(operation: WorkflowOperation): string {
    const labels: Record<string, string> = {
      WF02_CLASSIFICATION: 'WF-02',
      WF03_EVENT_MATCHING: 'WF-03',
      WF04_ANALYSIS: 'WF-04',
      WF05_CONTENT: 'WF-05',
      WF06_PUBLICATION_TELEGRAM: 'WF-06'
    };
    return labels[operation.workflowCode] ?? operation.workflowCode;
  }

  protected operationDetailEntries(operation: WorkflowOperation): { label: string; value: string }[] {
    const labels: Record<string, string> = {
      newsTitle: 'Noticia',
      category: 'Categoria',
      subcategory: 'Subcategoria',
      relevance: 'Relevancia',
      impact: 'Impacto',
      urgency: 'Urgencia',
      keywords: 'Keywords',
      entities: 'Entidades',
      aiSummary: 'Resumen IA',
      finalNewsStatus: 'Estado noticia',
      discardReason: 'Motivo descarte',
      candidateCount: 'Eventos candidatos',
      confidence: 'Confianza',
      automaticMatchThreshold: 'Umbral automatico',
      decision: 'Decision',
      finalEventId: 'Evento final',
      reason: 'Razon IA',
      newsCount: 'Noticias analizadas',
      analysisId: 'Analisis',
      executiveSummary: 'Resumen ejecutivo',
      unionSummary: 'Resumen sindical',
      keyPoints: 'Puntos clave',
      risks: 'Riesgos',
      opportunities: 'Oportunidades',
      contentId: 'Contenido',
      channel: 'Canal',
      tone: 'Tono',
      length: 'Longitud',
      title: 'Titulo',
      excerpt: 'Extracto',
      hashtags: 'Hashtags',
      editorialStatus: 'Estado editorial',
      publicationId: 'Publicacion',
      publicationStatus: 'Estado publicacion',
      triggerType: 'Tipo',
      externalId: 'Mensaje externo',
      scheduledAt: 'Programada',
      publishedAt: 'Publicada',
      auditDetail: 'Detalle auditoria',
      error: 'Error'
    };
    const hiddenKeys = new Set(['workflowCode', 'newsId', 'eventId', 'createdBy', 'auditAction', 'discarded', 'aiMatch', 'aiSuggestedEventId', 'created', 'matched', 'eventTitle', 'eventCategory', 'eventImportance', 'modelUsed', 'contentTitle']);
    return Object.entries(operation.details ?? {})
      .filter(([key, value]) => !hiddenKeys.has(key) && value !== null && value !== undefined && this.formatDetailValue(value) !== '')
      .map(([key, value]) => ({ label: labels[key] ?? key, value: this.formatDetailValue(value) }));
  }

  private updateForm(workflowCode: AutomationWorkflowCode, patch: Partial<AutomationSettingForm>): void {
    this.forms.update((forms) => ({
      ...forms,
      [workflowCode]: {
        ...this.formFor(workflowCode),
        ...patch
      }
    }));
  }

  private toAutomationWorkflowCode(workflowCode: string): AutomationWorkflowCode | null {
    const mappings: Record<string, AutomationWorkflowCode> = {
      WF02_CLASSIFICATION: 'WF02_CLASSIFICATION',
      WF03_EVENT_MATCHING: 'WF03_EVENT_DETECTION',
      WF04_ANALYSIS: 'WF04_ANALYSIS'
    };
    return mappings[workflowCode] ?? null;
  }

  private toForm(setting: AutomationWorkflowSetting): AutomationSettingForm {
    return {
      enabled: setting.enabled,
      intervalMinutes: Math.max(1, Math.round(setting.intervalSeconds / 60)),
      batchSize: setting.batchSize
    };
  }

  private filterPrompts(prompts: AiPromptVersion[]): AiPromptVersion[] {
    return prompts
      .filter((prompt) => this.matchesText(prompt.promptKey, this.promptKeyFilter()))
      .filter((prompt) => this.matchesText(prompt.promptName, this.promptNameFilter()))
      .filter((prompt) => this.matchesText(prompt.module, this.promptModuleFilter()))
      .filter((prompt) => this.matchesText(prompt.version, this.promptVersionFilter()))
      .filter((prompt) => this.matchesText(prompt.checksum, this.promptChecksumFilter()))
      .filter((prompt) => this.matchesOption(this.activeLabel(prompt.active), this.promptActiveFilter()))
      .filter((prompt) => this.matchesText(this.formatDate(prompt.createdAt), this.promptCreatedAtFilter()));
  }

  private sortPrompts(prompts: AiPromptVersion[]): AiPromptVersion[] {
    const direction = this.promptSortDirection() === 'asc' ? 1 : -1;
    const column = this.promptSortColumn();
    return [...prompts].sort((left, right) => direction * this.comparePrompts(left, right, column));
  }

  private comparePrompts(left: AiPromptVersion, right: AiPromptVersion, column: PromptSortColumn): number {
    if (column === 'active') {
      return Number(left.active) - Number(right.active);
    }
    if (column === 'createdAt') {
      return new Date(left.createdAt).getTime() - new Date(right.createdAt).getTime();
    }
    return left[column].localeCompare(right[column], 'es', { sensitivity: 'base' });
  }

  private filterMetrics(metrics: WorkflowOperation[]): WorkflowOperation[] {
    return metrics
      .filter((metric) => this.matchesText(`${metric.workflowCode} ${metric.operationType}`, this.metricOperationFilter()))
      .filter((metric) => this.matchesText(metric.promptKey ?? '-', this.metricPromptFilter()))
      .filter((metric) => this.matchesText(metric.provider ?? '-', this.metricProviderFilter()))
      .filter((metric) => this.matchesText(metric.model ?? '-', this.metricModelFilter()))
      .filter((metric) => this.matchesOption(this.metricStatusLabel(metric.status), this.metricStatusFilter()))
      .filter((metric) => this.matchesText(metric.relatedEntityType ?? '-', this.metricRelatedEntityTypeFilter()))
      .filter((metric) => this.matchesText(metric.latencyMs?.toString() ?? '-', this.metricLatencyFilter()))
      .filter((metric) => this.matchesText(metric.errorMessage ?? '-', this.metricErrorFilter()))
      .filter((metric) => this.matchesText(this.formatDate(metric.createdAt), this.metricCreatedAtFilter()));
  }

  private sortMetrics(metrics: WorkflowOperation[]): WorkflowOperation[] {
    const direction = this.metricSortDirection() === 'asc' ? 1 : -1;
    const column = this.metricSortColumn();
    return [...metrics].sort((left, right) => direction * this.compareMetrics(left, right, column));
  }

  private compareMetrics(left: WorkflowOperation, right: WorkflowOperation, column: MetricSortColumn): number {
    if (column === 'latencyMs') {
      return (left[column] ?? -1) - (right[column] ?? -1);
    }
    if (column === 'createdAt') {
      return new Date(left.createdAt).getTime() - new Date(right.createdAt).getTime();
    }
    return this.metricValue(left, column).localeCompare(this.metricValue(right, column), 'es', { sensitivity: 'base' });
  }

  private metricValue(metric: WorkflowOperation, column: Exclude<MetricSortColumn, 'latencyMs' | 'createdAt'>): string {
    const values = {
      workflowCode: metric.workflowCode,
      operationType: metric.operationType,
      promptKey: metric.promptKey ?? '-',
      provider: metric.provider ?? '-',
      model: metric.model ?? '-',
      status: this.metricStatusLabel(metric.status),
      relatedEntityType: metric.relatedEntityType ?? '-',
      errorMessage: metric.errorMessage ?? '-'
    };
    return values[column];
  }

  private toAiMetricCards(metrics: AiMetricsSnapshot | null): MetricCard[] {
    const snapshot = metrics ?? this.emptyMetrics();
    const updatedAt = new Date().toISOString();
    return [
      {
        label: 'Operaciones IA',
        value: snapshot.totalOperations.toString(),
        trend: this.formatSigned(snapshot.totalDifference),
        tone: 'primary',
        todayValue: snapshot.totalOperations,
        yesterdayValue: snapshot.previousTotalOperations,
        difference: snapshot.totalDifference,
        title: 'Operaciones IA',
        subtitle: `Dia ${this.metricDate()}`,
        icon: 'total',
        badgeLabel: 'Diario',
        lastUpdatedAt: updatedAt,
        items: [
          { label: 'Hoy', value: snapshot.totalOperations, tone: 'primary', icon: 'calendar', signed: false },
          { label: 'Ayer', value: snapshot.previousTotalOperations, tone: 'neutral', icon: 'clock', signed: false },
          { label: 'Diferencia', value: snapshot.totalDifference, tone: 'primary', icon: 'trend', signed: true }
        ]
      },
      {
        label: 'Calidad',
        value: `${snapshot.successRate}%`,
        trend: this.formatSigned(snapshot.successRateDifference),
        tone: 'success',
        todayValue: snapshot.successCount,
        yesterdayValue: snapshot.previousSuccessCount,
        difference: snapshot.successRateDifference,
        title: 'Calidad',
        subtitle: 'Exito y fallos del dia',
        icon: 'check',
        badgeLabel: `${snapshot.successRate}%`,
        lastUpdatedAt: updatedAt,
        items: [
          { label: 'Correctas', value: snapshot.successCount, tone: 'success', icon: 'check', signed: false },
          { label: 'Fallidas', value: snapshot.failedCount, tone: snapshot.failedCount > 0 ? 'danger' : 'success', icon: 'x', signed: false },
          { label: 'Exito %', value: snapshot.successRate, tone: 'success', icon: 'target', signed: false }
        ]
      },
      {
        label: 'Errores',
        value: `${snapshot.failureRate}%`,
        trend: this.formatSigned(snapshot.failureRateDifference),
        tone: snapshot.failedCount > 0 ? 'danger' : 'success',
        todayValue: snapshot.failedCount,
        yesterdayValue: snapshot.previousFailedCount,
        difference: snapshot.failureRateDifference,
        title: 'Errores',
        subtitle: 'Tasa de fallo diaria',
        icon: 'alert',
        badgeLabel: `${snapshot.failureRate}%`,
        lastUpdatedAt: updatedAt,
        items: [
          { label: 'Fallidas', value: snapshot.failedCount, tone: snapshot.failedCount > 0 ? 'danger' : 'success', icon: 'x', signed: false },
          { label: 'Fallo %', value: snapshot.failureRate, tone: snapshot.failureRate > 0 ? 'danger' : 'success', icon: 'alert', signed: false },
          { label: 'Dif. tasa', value: snapshot.failureRateDifference, tone: snapshot.failureRateDifference > 0 ? 'danger' : 'success', icon: 'trend', signed: true }
        ]
      },
      {
        label: 'Latencia',
        value: `${snapshot.averageLatencyMs} ms`,
        trend: this.formatSigned(snapshot.averageLatencyDifference),
        tone: snapshot.averageLatencyDifference > 0 ? 'warning' : 'primary',
        todayValue: snapshot.averageLatencyMs,
        yesterdayValue: snapshot.previousAverageLatencyMs,
        difference: snapshot.averageLatencyDifference,
        title: 'Latencia',
        subtitle: 'Rendimiento IA',
        icon: 'clock',
        badgeLabel: 'ms',
        lastUpdatedAt: updatedAt,
        items: [
          { label: 'Media ms', value: snapshot.averageLatencyMs, tone: 'primary', icon: 'clock', signed: false },
          { label: 'P95 ms', value: snapshot.p95LatencyMs, tone: 'warning', icon: 'trend', signed: false },
          { label: 'Dif. media', value: snapshot.averageLatencyDifference, tone: snapshot.averageLatencyDifference > 0 ? 'warning' : 'success', icon: 'trend', signed: true }
        ]
      }
    ];
  }

  private emptyMetrics(): AiMetricsSnapshot {
    return {
      totalOperations: 0,
      successCount: 0,
      failedCount: 0,
      averageLatencyMs: 0,
      p95LatencyMs: 0,
      successRate: 0,
      failureRate: 0,
      previousTotalOperations: 0,
      previousSuccessCount: 0,
      previousFailedCount: 0,
      previousAverageLatencyMs: 0,
      totalDifference: 0,
      successRateDifference: 0,
      failureRateDifference: 0,
      averageLatencyDifference: 0,
      recentMetrics: []
    };
  }

  private formatSigned(value: number): string {
    return value > 0 ? `+${value}` : value.toString();
  }

  private detailNumber(operation: WorkflowOperation, key: string): number | null {
    const value = operation.details?.[key];
    return typeof value === 'number' ? value : null;
  }

  private formatDetailValue(value: unknown): string {
    if (Array.isArray(value)) {
      return value.map((item) => this.formatDetailValue(item)).filter(Boolean).join(', ');
    }
    if (typeof value === 'string') {
      if (/^\d{4}-\d{2}-\d{2}T/.test(value)) {
        return this.formatDate(value);
      }
      return value;
    }
    if (typeof value === 'number' || typeof value === 'boolean') {
      return value.toString();
    }
    return '';
  }

  private todayInputValue(): string {
    const now = new Date();
    const year = now.getFullYear();
    const month = `${now.getMonth() + 1}`.padStart(2, '0');
    const day = `${now.getDate()}`.padStart(2, '0');
    return `${year}-${month}-${day}`;
  }

  private matchesText(value: string, filter: string): boolean {
    const normalizedFilter = filter.trim().toLocaleLowerCase('es');
    return !normalizedFilter || value.trim().toLocaleLowerCase('es').includes(normalizedFilter);
  }

  private matchesOption(value: string, filter: string): boolean {
    const normalizedFilter = filter.trim().toLocaleLowerCase('es');
    return !normalizedFilter || value.trim().toLocaleLowerCase('es') === normalizedFilter;
  }

  private activeLabel(active: boolean): string {
    return active ? 'Activo' : 'Inactivo';
  }
}
