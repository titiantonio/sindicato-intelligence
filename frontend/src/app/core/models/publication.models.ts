import { ContentListItem } from './content.models';
import { EventDetail } from './event.models';

export interface PublicationListItem {
  id: number;
  contentId: number | null;
  channel: string;
  publicationType?: 'GENERATED_CONTENT' | 'MANUAL_MESSAGE';
  titleSnapshot?: string | null;
  messageSnapshot?: string | null;
  requestedBy?: number | null;
  requestedByName?: string | null;
  requestedByEmail?: string | null;
  externalId: string | null;
  status: string;
  publishedAt: string | null;
  responsePayload: string | null;
  scheduledAt: string | null;
  targets?: PublicationTarget[];
  attachments?: PublicationAttachment[];
}

export interface PublicationTarget {
  id: number;
  destinationId: number | null;
  destinationName: string;
  status: string;
  externalId: string | null;
  responsePayload: string | null;
  publishedAt: string | null;
}

export interface PublicationAttachment {
  id: number;
  originalFilename: string;
  mediaType: string;
  mimeType: string;
  fileSizeBytes: number;
  telegramMethod: string;
  position: number;
}

export interface PublicationDetail {
  publication: PublicationListItem;
  content: ContentListItem | null;
  event: EventDetail | null;
}

export interface ManualPublicationPayload {
  channel: string;
  title: string;
  message: string;
  destinationIds: number[];
  files: File[];
}

export interface OperationalTelegramDestination {
  id: number;
  name: string;
  defaultSelected: boolean;
}
