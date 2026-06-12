export interface PublicationListItem {
  id: number;
  contentId: number;
  channel: string;
  externalId: string | null;
  status: string;
  publishedAt: string | null;
  responsePayload: string | null;
}