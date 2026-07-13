export interface EventListItem {
  id: number;
  title: string;
  description: string | null;
  category: string;
  importance: string;
  status: string;
  editorialStatus: string;
  newsCount: number;
  firstDetectedAt: string;
  lastUpdatedAt: string;
  updatedAt: string;
}

export interface EventDetail extends EventListItem {
  createdAt: string;
  news: EventNewsItem[];
  analyses: EventAnalysisItem[];
  contents: EventContentItem[];
}

export interface EventNewsItem {
  id: number;
  sourceId: number;
  title: string;
  url: string;
  summary: string | null;
  processingStatus: string;
  publishedAt: string | null;
  capturedAt: string;
  classification: EventNewsClassification | null;
}

export interface EventNewsClassification {
  id: number;
  newsId: number;
  category: string;
  subcategory: string | null;
  relevanceScore: number;
  impactLevel: string;
  urgencyLevel: string;
  keywords: string[];
  entities: string[];
  classifiedAt: string;
}

export interface EventAnalysisItem {
  id: number;
  eventId: number;
  executiveSummary: string;
  unionSummary: string;
  keyPoints: string[];
  risks: string[];
  opportunities: string[];
  affectedGroups: string[];
  recommendedMonitoring: string[];
  analysisType: string;
  generationTrigger: string;
  eventUpdatedAtSnapshot: string;
  contextNewsCount: number;
  contextTruncated: boolean;
  outdated?: boolean;
  modelUsed: string;
  generatedAt: string;
}

export interface EventContentItem {
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
