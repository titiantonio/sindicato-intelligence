import { EventNewsClassification } from './event.models';

export interface NewsDetail {
  id: number;
  sourceId: number;
  title: string;
  url: string;
  summary: string | null;
  content: string | null;
  hash: string;
  publishedAt: string | null;
  capturedAt: string;
  processingStatus: string;
  createdAt: string;
  updatedAt: string;
  eventId: number | null;
  classification: EventNewsClassification | null;
}

export type NewsListItem = NewsDetail;
