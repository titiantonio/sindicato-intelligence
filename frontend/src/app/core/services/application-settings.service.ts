import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';

import {
  TelegramPublicationSettings,
  UpdateTelegramPublicationSettingsRequest
} from '../models/application-settings.models';

@Injectable({
  providedIn: 'root'
})
export class ApplicationSettingsService {
  private readonly httpClient = inject(HttpClient);

  getTelegramSettings() {
    return this.httpClient.get<TelegramPublicationSettings>('/api/v1/settings/telegram');
  }

  updateTelegramSettings(payload: UpdateTelegramPublicationSettingsRequest) {
    return this.httpClient.put<TelegramPublicationSettings>('/api/v1/settings/telegram', payload);
  }
}
