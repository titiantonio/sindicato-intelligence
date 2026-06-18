export interface AiPromptVersion {
  promptKey: string;
  promptName: string;
  module: string;
  version: string;
  checksum: string;
  active: boolean;
  createdAt: string;
}

export interface AiMetric {
  id: number;
  operationType: string;
  promptKey: string;
  provider: string;
  model: string | null;
  status: 'SUCCESS' | 'FAILED';
  relatedEntityType: string | null;
  relatedEntityId: number | null;
  latencyMs: number;
  errorMessage: string | null;
  createdAt: string;
}

export interface AiMetricsSnapshot {
  totalOperations: number;
  successCount: number;
  failedCount: number;
  averageLatencyMs: number;
  p95LatencyMs: number;
  successRate: number;
  failureRate: number;
  previousTotalOperations: number;
  previousSuccessCount: number;
  previousFailedCount: number;
  previousAverageLatencyMs: number;
  totalDifference: number;
  successRateDifference: number;
  failureRateDifference: number;
  averageLatencyDifference: number;
  recentMetrics: AiMetric[];
}
