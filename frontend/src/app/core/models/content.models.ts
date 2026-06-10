export interface ContentListItem {
  id: number;
  channel: string;
  title: string;
  status: string;
  createdAt: string;
  approvedAt: string | null;
}
