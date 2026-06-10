export interface MetricCard {
  label: string;
  value: string;
  trend: string;
  tone: 'primary' | 'success' | 'warning' | 'danger';
}

export interface PriorityEvent {
  id: number;
  title: string;
  category: string;
  importance: string;
  relatedNews: number;
  updatedAt: string;
  status: string;
}
