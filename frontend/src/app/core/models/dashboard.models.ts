export interface MetricCardItem {
  label: string;
  value: number;
  tone: 'primary' | 'success' | 'warning' | 'danger' | 'neutral' | 'purple';
  icon: string;
  signed: boolean;
}

export interface MetricCard {
  label: string;
  value: string;
  trend: string;
  tone: 'primary' | 'success' | 'warning' | 'danger';
  todayValue: number;
  yesterdayValue: number;
  difference: number;
  title: string;
  subtitle: string;
  icon: string;
  badgeLabel: string;
  lastUpdatedAt: string;
  items: MetricCardItem[];
}

export interface PriorityEvent {
  id: number;
  title: string;
  category: string;
  importance: string;
  relatedNews: number;
  updatedAt: string;
  status: string;
  editorialStatus: string;
}
