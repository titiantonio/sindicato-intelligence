export interface TelegramPublicationDestination {
  id: number | null;
  name: string;
  chatId: string;
  active: boolean;
  defaultSelected: boolean;
  updatedAt?: string;
}

export interface TelegramPublicationSettings {
  enabled: boolean;
  baseUrl: string;
  chatId: string | null;
  disableWebPagePreview: boolean;
  botTokenConfigured: boolean;
  botTokenPreview: string | null;
  readyToPublish: boolean;
  updatedAt: string;
  destinations?: TelegramPublicationDestination[];
}

export interface UpdateTelegramPublicationSettingsRequest {
  enabled: boolean;
  baseUrl: string;
  botToken: string | null;
  chatId: string | null;
  disableWebPagePreview: boolean;
  destinations?: TelegramPublicationDestination[];
}
