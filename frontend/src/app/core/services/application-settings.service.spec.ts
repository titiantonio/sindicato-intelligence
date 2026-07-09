import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { TestBed } from '@angular/core/testing';

import { ApplicationSettingsService } from './application-settings.service';

describe('ApplicationSettingsService', () => {
  let service: ApplicationSettingsService;
  let httpTestingController: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [provideHttpClient(), provideHttpClientTesting()]
    });

    service = TestBed.inject(ApplicationSettingsService);
    httpTestingController = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpTestingController.verify();
  });

  it('reads telegram settings', () => {
    service.getTelegramSettings().subscribe();

    const request = httpTestingController.expectOne('/api/v1/settings/telegram');
    expect(request.request.method).toBe('GET');
    request.flush({ enabled: false, baseUrl: 'https://api.telegram.org' });
  });

  it('updates telegram settings', () => {
    const payload = {
      enabled: true,
      baseUrl: 'https://api.telegram.org',
      botToken: 'token',
      chatId: 'chat-id',
      disableWebPagePreview: true,
      maxAttachmentCount: 10,
      maxAttachmentFileBytes: 20971520,
      maxAttachmentTotalBytes: 52428800
    };

    service.updateTelegramSettings(payload).subscribe();

    const request = httpTestingController.expectOne('/api/v1/settings/telegram');
    expect(request.request.method).toBe('PUT');
    expect(request.request.body).toEqual(payload);
    request.flush({ ...payload, readyToPublish: true });
  });
});
