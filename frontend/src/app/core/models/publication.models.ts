import { ContentListItem } from './content.models';
import { EventDetail } from './event.models';

export interface PublicationListItem {
  id: number;
  contentId: number;
  channel: string;
  externalId: string | null;
  status: string;
  publishedAt: string | null;
  responsePayload: string | null;
  scheduledAt: string | null;
}

export interface PublicationDetail {
  publication: PublicationListItem;
  content: ContentListItem;
  event: EventDetail;
}
