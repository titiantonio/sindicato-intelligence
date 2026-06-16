import { ComponentFixture, TestBed } from '@angular/core/testing';
import { of } from 'rxjs';

import { AutomationWorkflowSetting } from '../../core/models/automation.models';
import { ApplicationSettingsService } from '../../core/services/application-settings.service';
import { AutomationService } from '../../core/services/automation.service';
import { AutomationSettingsPageComponent } from './automation-settings-page.component';

describe('AutomationSettingsPageComponent', () => {
  let fixture: ComponentFixture<AutomationSettingsPageComponent>;
  let applicationSettingsService: jasmine.SpyObj<ApplicationSettingsService>;
  let automationService: jasmine.SpyObj<AutomationService>;

  beforeEach(async () => {
    applicationSettingsService = jasmine.createSpyObj<ApplicationSettingsService>('ApplicationSettingsService', ['getTelegramSettings', 'updateTelegramSettings']);
    automationService = jasmine.createSpyObj<AutomationService>('AutomationService', ['listSettings', 'updateSetting', 'runWorkflow']);
    applicationSettingsService.getTelegramSettings.and.returnValue(of(telegramSettings()));
    applicationSettingsService.updateTelegramSettings.and.returnValue(of({ ...telegramSettings(), enabled: true, readyToPublish: true }));
    automationService.listSettings.and.returnValue(of([setting()]));
    automationService.updateSetting.and.returnValue(of(setting()));
    automationService.runWorkflow.and.returnValue(of({ processedCount: 1, successCount: 1, failedCount: 0, skippedCount: 0, errors: [] }));

    await TestBed.configureTestingModule({
      imports: [AutomationSettingsPageComponent],
      providers: [
        { provide: ApplicationSettingsService, useValue: applicationSettingsService },
        { provide: AutomationService, useValue: automationService }
      ]
    }).compileComponents();

    fixture = TestBed.createComponent(AutomationSettingsPageComponent);
    fixture.detectChanges();
  });

  it('renders settings and editable scheduling fields', () => {
    const compiled = fixture.nativeElement as HTMLElement;

    expect(compiled.textContent).toContain('Telegram');
    expect(compiled.textContent).toContain('WF02 - Clasificacion');
    expect(compiled.querySelector<HTMLInputElement>('input[type="number"]')?.value).toBe('10');
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
});
