import { EventDetail } from './event.models';

export interface ContentListItem {
  id: number;
  eventId: number;
  analysisId: number | null;
  createdBy: number;
  channel: string;
  tone: string;
  contentType: string;
  length: string;
  title: string;
  content: string;
  status: string;
  generatedAt: string;
  approvedAt: string | null;
  generationMetadata: Record<string, unknown>;
}

export interface ContentDetail {
  content: ContentListItem;
  event: EventDetail;
}
