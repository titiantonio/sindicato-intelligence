import { EventNewsClassification } from './event.models';

export interface NewsDetail {
  id: number;
  sourceId: number;
  sourceName: string | null;
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

export interface NewsPageItem {
  id: number;
  sourceId: number;
  sourceName: string | null;
  title: string;
  url: string;
  processingStatus: string;
  eventId: number | null;
  category: string | null;
  publishedAt: string | null;
  capturedAt: string;
}

export interface NewsPageResponse {
  items: NewsPageItem[];
  page: number;
  pageSize: number;
  totalItems: number;
  totalPages: number;
}

export interface NewsPageParams {
  page: number;
  pageSize: number;
  global?: string;
  id?: string;
  title?: string;
  source?: string;
  status?: string;
  event?: string;
  category?: string;
  publishedAt?: string;
  capturedAt?: string;
  sortColumn: string;
  sortDirection: string;
}

export type NewsListItem = NewsPageItem;
