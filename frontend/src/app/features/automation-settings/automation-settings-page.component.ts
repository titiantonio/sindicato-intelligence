import { Component, OnInit, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AutomationRunResult, AutomationWorkflowCode, AutomationWorkflowSetting } from '../../core/models/automation.models';
import { AutomationService } from '../../core/services/automation.service';

interface AutomationSettingForm {
  enabled: boolean;
  intervalMinutes: number;
  batchSize: number;
}

@Component({
  selector: 'app-automation-settings-page',
  imports: [FormsModule],
  templateUrl: './automation-settings-page.component.html',
  styleUrl: './automation-settings-page.component.scss'
})
export class AutomationSettingsPageComponent implements OnInit {
  private readonly automationService = inject(AutomationService);

  protected readonly settings = signal<AutomationWorkflowSetting[]>([]);
  protected readonly forms = signal<Record<string, AutomationSettingForm>>({});
  protected readonly isLoading = signal(false);
  protected readonly busyWorkflow = signal<AutomationWorkflowCode | null>(null);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly successMessage = signal<string | null>(null);
  protected readonly lastRunResult = signal<Record<string, AutomationRunResult>>({});

  ngOnInit(): void {
    this.loadSettings();
  }

  protected loadSettings(): void {
    this.isLoading.set(true);
    this.errorMessage.set(null);

    this.automationService.listSettings().subscribe({
      next: (settings) => {
        this.settings.set(settings);
        this.forms.set(Object.fromEntries(settings.map((setting) => [setting.workflowCode, this.toForm(setting)])));
        this.isLoading.set(false);
      },
      error: (error: { error?: { error?: string } }) => {
        this.errorMessage.set(error.error?.error ?? 'No se pudo cargar la configuracion de automatizaciones.');
        this.isLoading.set(false);
      }
    });
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
      return `${liveResult.processedCount}/${liveResult.successCount}/${liveResult.failedCount}/${liveResult.skippedCount}`;
    }
    return `${setting.lastProcessedCount}/${setting.lastSuccessCount}/${setting.lastFailedCount}/${setting.lastSkippedCount}`;
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

  private toForm(setting: AutomationWorkflowSetting): AutomationSettingForm {
    return {
      enabled: setting.enabled,
      intervalMinutes: Math.max(1, Math.round(setting.intervalSeconds / 60)),
      batchSize: setting.batchSize
    };
  }
}
