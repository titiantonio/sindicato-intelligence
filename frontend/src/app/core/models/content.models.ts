export interface ContentListItem {
  id: number;
  eventId: number;
  createdBy: number;
  channel: string;
  tone: string;
  title: string;
  content: string;
  status: string;
  generatedAt: string;
  approvedAt: string | null;
}