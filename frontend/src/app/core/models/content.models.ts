import { EventDetail } from './event.models';

export interface ContentListItem {
  id: number;
  eventId: number;
  analysisId: number | null;
  createdBy: number;
  channel: string;
  tone: string;
  title: string;
  content: string;
  status: string;
  generatedAt: string;
  approvedAt: string | null;
}

export interface ContentDetail {
  content: ContentListItem;
  event: EventDetail;
}
