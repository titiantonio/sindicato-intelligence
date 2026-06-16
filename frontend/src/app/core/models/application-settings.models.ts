export interface TelegramPublicationSettings {
  enabled: boolean;
  baseUrl: string;
  chatId: string | null;
  disableWebPagePreview: boolean;
  botTokenConfigured: boolean;
  botTokenPreview: string | null;
  readyToPublish: boolean;
  updatedAt: string;
}

export interface UpdateTelegramPublicationSettingsRequest {
  enabled: boolean;
  baseUrl: string;
  botToken: string | null;
  chatId: string | null;
  disableWebPagePreview: boolean;
}
