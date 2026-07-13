CREATE INDEX idx_ai_operation_metrics_entity_failures
    ON ai_operation_metrics (prompt_key, related_entity_type, related_entity_id, status, created_at DESC);
