CREATE INDEX idx_news_articles_captured_visible ON news_articles (processing_status, captured_at DESC, id DESC);
CREATE INDEX idx_news_articles_updated_captured ON news_articles (updated_at DESC, captured_at DESC);

CREATE INDEX idx_events_dashboard_priority ON events (status, manual_discarded, category, importance, last_updated_at DESC, id DESC);
CREATE INDEX idx_events_first_detected ON events (status, first_detected_at DESC, id DESC);
CREATE INDEX idx_events_updated_last_updated ON events (updated_at DESC, last_updated_at DESC);

CREATE INDEX idx_generated_content_event_status_generated ON generated_content (event_id, status, generated_at DESC, id DESC);
CREATE INDEX idx_generated_content_status_generated ON generated_content (status, generated_at DESC, id DESC);

CREATE INDEX idx_publications_content_status_published ON publications (content_id, publication_status, published_at DESC, id DESC);
CREATE INDEX idx_publications_status_scheduled ON publications (publication_status, scheduled_at DESC, id DESC);
CREATE INDEX idx_publications_published_at ON publications (published_at DESC);

CREATE INDEX idx_audit_log_entity_action_created ON audit_log (entity_type, action, created_at DESC, id DESC);
CREATE INDEX idx_ai_operation_metrics_created_id ON ai_operation_metrics (created_at DESC, id DESC);
