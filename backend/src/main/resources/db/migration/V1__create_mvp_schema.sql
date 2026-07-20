CREATE TABLE sources
(
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    url TEXT NOT NULL,
    type VARCHAR(50) NOT NULL,
    priority INTEGER NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_sources_url UNIQUE (url)
);

CREATE TABLE users
(
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    name VARCHAR(255) NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    must_change_password BOOLEAN NOT NULL DEFAULT FALSE,
    status VARCHAR(50) NOT NULL,
    temporary_password_expires_at TIMESTAMP WITH TIME ZONE,
    last_login_at TIMESTAMP WITH TIME ZONE,
    last_password_change_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_email UNIQUE (email),
    CONSTRAINT ck_users_role CHECK (role IN ('ADMIN', 'EDITOR')),
    CONSTRAINT ck_users_status CHECK (status IN ('PENDING_ACTIVATION', 'ACTIVE', 'INACTIVE', 'LOCKED'))
);

CREATE TABLE user_password_history
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_password_history_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE user_audit_log
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    actor_email VARCHAR(255),
    action VARCHAR(100) NOT NULL,
    details TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_user_audit_log_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE password_reset_tokens
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    used_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_password_reset_tokens_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_password_reset_tokens_token UNIQUE (token)
);

CREATE TABLE refresh_tokens
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    token_id VARCHAR(64) NOT NULL,
    token_hash VARCHAR(64) NOT NULL,
    issued_at TIMESTAMP WITH TIME ZONE NOT NULL,
    expires_at TIMESTAMP WITH TIME ZONE NOT NULL,
    revoked_at TIMESTAMP WITH TIME ZONE,
    replaced_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_refresh_tokens_user FOREIGN KEY (user_id) REFERENCES users (id),
    CONSTRAINT uk_refresh_tokens_token_id UNIQUE (token_id),
    CONSTRAINT ck_refresh_tokens_token_id CHECK (token_id <> ''),
    CONSTRAINT ck_refresh_tokens_token_hash CHECK (token_hash <> '')
);

CREATE TABLE news_articles
(
    id BIGSERIAL PRIMARY KEY,
    source_id BIGINT NOT NULL,
    title TEXT NOT NULL,
    url TEXT NOT NULL,
    summary TEXT,
    content TEXT,
    hash VARCHAR(64) NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    captured_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    processing_status VARCHAR(50) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_news_articles_source FOREIGN KEY (source_id) REFERENCES sources (id),
    CONSTRAINT uk_news_articles_url UNIQUE (url),
    CONSTRAINT uk_news_articles_hash UNIQUE (hash),
    CONSTRAINT ck_news_articles_processing_status CHECK (processing_status IN ('CAPTURED', 'CLASSIFIED', 'EVENT_MATCHED', 'ARCHIVED', 'DISCARDED'))
);

CREATE TABLE news_classifications
(
    id BIGSERIAL PRIMARY KEY,
    news_id BIGINT NOT NULL,
    category VARCHAR(100) NOT NULL,
    subcategory VARCHAR(100),
    relevance_score NUMERIC(5,2) NOT NULL,
    impact_level VARCHAR(50) NOT NULL,
    urgency_level VARCHAR(50) NOT NULL,
    keywords JSONB,
    entities JSONB,
    classified_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_news_classifications_news FOREIGN KEY (news_id) REFERENCES news_articles (id),
    CONSTRAINT uk_news_classifications_news UNIQUE (news_id),
    CONSTRAINT ck_news_classifications_relevance CHECK (relevance_score >= 0 AND relevance_score <= 100)
);

CREATE TABLE events
(
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(500) NOT NULL,
    description TEXT,
    category VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    importance VARCHAR(50) NOT NULL,
    first_detected_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    last_updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    manual_discarded BOOLEAN NOT NULL DEFAULT FALSE,
    manual_discarded_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT ck_events_status CHECK (status IN ('OPEN', 'MONITORING', 'CLOSED', 'ARCHIVED')),
    CONSTRAINT ck_events_importance CHECK (importance IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL'))
);

CREATE TABLE event_news
(
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    confidence_score INTEGER,
    match_decision VARCHAR(80),
    match_reason TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_event_news_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_event_news_news FOREIGN KEY (news_id) REFERENCES news_articles (id),
    CONSTRAINT uk_event_news_event_news UNIQUE (event_id, news_id),
    CONSTRAINT ck_event_news_confidence_score CHECK (confidence_score IS NULL OR confidence_score BETWEEN 0 AND 100)
);

CREATE TABLE event_ai_analysis
(
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    executive_summary TEXT NOT NULL,
    union_summary TEXT NOT NULL,
    key_points JSONB,
    risks JSONB,
    opportunities JSONB,
    affected_groups JSONB,
    recommended_monitoring JSONB,
    analysis_type VARCHAR(40) NOT NULL DEFAULT 'STANDARD',
    generation_trigger VARCHAR(40) NOT NULL DEFAULT 'BATCH',
    event_updated_at_snapshot TIMESTAMP WITH TIME ZONE NOT NULL,
    context_news_count INTEGER NOT NULL DEFAULT 0,
    context_truncated BOOLEAN NOT NULL DEFAULT FALSE,
    model_used VARCHAR(100) NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_event_ai_analysis_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT ck_event_ai_analysis_type CHECK (analysis_type IN ('QUICK', 'STANDARD', 'PRIORITY', 'CRISIS')),
    CONSTRAINT ck_event_ai_analysis_trigger CHECK (generation_trigger IN ('BATCH', 'MANUAL', 'PRIORITY_AUTO', 'REANALYSIS')),
    CONSTRAINT ck_event_ai_analysis_context_news_count CHECK (context_news_count >= 0)
);

CREATE TABLE generated_content
(
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    analysis_id BIGINT,
    created_by BIGINT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    tone VARCHAR(50) NOT NULL,
    content_type VARCHAR(40) NOT NULL DEFAULT 'TELEGRAM_POST',
    length VARCHAR(40) NOT NULL DEFAULT 'STANDARD',
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    approved_at TIMESTAMP WITH TIME ZONE,
    generation_metadata JSONB NOT NULL DEFAULT '{}'::jsonb,
    CONSTRAINT fk_generated_content_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_generated_content_analysis FOREIGN KEY (analysis_id) REFERENCES event_ai_analysis (id),
    CONSTRAINT fk_generated_content_created_by FOREIGN KEY (created_by) REFERENCES users (id),
    CONSTRAINT ck_generated_content_status CHECK (status IN ('GENERATED', 'PENDING_REVIEW', 'APPROVED', 'REJECTED', 'PUBLISHED')),
    CONSTRAINT ck_generated_content_type CHECK (content_type IN ('TELEGRAM_POST', 'TELEGRAM_SHORT', 'UNION_STATEMENT'))
);

CREATE TABLE publications
(
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT,
    channel VARCHAR(50) NOT NULL,
    publication_type VARCHAR(50) NOT NULL DEFAULT 'GENERATED_CONTENT',
    title_snapshot VARCHAR(500),
    message_snapshot TEXT,
    requested_by BIGINT,
    external_id VARCHAR(255),
    publication_status VARCHAR(50) NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    scheduled_at TIMESTAMP WITH TIME ZONE,
    response_payload JSONB,
    CONSTRAINT fk_publications_content FOREIGN KEY (content_id) REFERENCES generated_content (id),
    CONSTRAINT fk_publications_requested_by FOREIGN KEY (requested_by) REFERENCES users (id),
    CONSTRAINT ck_publications_status CHECK (publication_status IN ('PENDING', 'SCHEDULED', 'PUBLISHED', 'FAILED')),
    CONSTRAINT ck_publications_type CHECK (publication_type IN ('GENERATED_CONTENT', 'MANUAL_MESSAGE')),
    CONSTRAINT ck_publications_content_or_manual CHECK (
        (publication_type = 'GENERATED_CONTENT' AND content_id IS NOT NULL)
        OR
        (publication_type = 'MANUAL_MESSAGE' AND content_id IS NULL)
    )
);

CREATE TABLE audit_log
(
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT,
    action VARCHAR(100) NOT NULL,
    entity_type VARCHAR(100) NOT NULL,
    entity_id BIGINT,
    old_values TEXT,
    new_values TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_audit_log_user FOREIGN KEY (user_id) REFERENCES users (id)
);

CREATE TABLE automation_workflow_settings
(
    workflow_code VARCHAR(50) PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    interval_seconds INTEGER NOT NULL,
    batch_size INTEGER NOT NULL,
    running BOOLEAN NOT NULL DEFAULT FALSE,
    last_run_at TIMESTAMP WITH TIME ZONE,
    last_success_at TIMESTAMP WITH TIME ZONE,
    last_failure_at TIMESTAMP WITH TIME ZONE,
    next_run_at TIMESTAMP WITH TIME ZONE NOT NULL,
    last_processed_count INTEGER NOT NULL DEFAULT 0,
    last_success_count INTEGER NOT NULL DEFAULT 0,
    last_failed_count INTEGER NOT NULL DEFAULT 0,
    last_skipped_count INTEGER NOT NULL DEFAULT 0,
    last_error TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_automation_workflow_settings_interval CHECK (interval_seconds >= 60),
    CONSTRAINT ck_automation_workflow_settings_batch_size CHECK (batch_size >= 1)
);

CREATE TABLE telegram_publication_settings
(
    id SMALLINT PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    base_url VARCHAR(255) NOT NULL,
    bot_token VARCHAR(255),
    chat_id VARCHAR(100),
    disable_web_page_preview BOOLEAN NOT NULL DEFAULT TRUE,
    max_attachment_count INTEGER NOT NULL DEFAULT 10,
    max_attachment_file_bytes BIGINT NOT NULL DEFAULT 20971520,
    max_attachment_total_bytes BIGINT NOT NULL DEFAULT 52428800,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_telegram_publication_settings_singleton CHECK (id = 1),
    CONSTRAINT ck_telegram_publication_settings_base_url CHECK (base_url <> ''),
    CONSTRAINT ck_telegram_publication_settings_max_attachment_count CHECK (max_attachment_count > 0),
    CONSTRAINT ck_telegram_publication_settings_max_attachment_file_bytes CHECK (max_attachment_file_bytes > 0),
    CONSTRAINT ck_telegram_publication_settings_max_attachment_total_bytes CHECK (max_attachment_total_bytes > 0),
    CONSTRAINT ck_telegram_publication_settings_max_attachment_total_ge_file CHECK (max_attachment_total_bytes >= max_attachment_file_bytes)
);

CREATE TABLE telegram_publication_destinations
(
    id BIGSERIAL PRIMARY KEY,
    settings_id SMALLINT NOT NULL,
    name VARCHAR(120) NOT NULL,
    chat_id VARCHAR(100) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    default_selected BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_telegram_destinations_settings FOREIGN KEY (settings_id) REFERENCES telegram_publication_settings (id),
    CONSTRAINT ck_telegram_destinations_name CHECK (name <> ''),
    CONSTRAINT ck_telegram_destinations_chat_id CHECK (chat_id <> '')
);

CREATE TABLE publication_targets
(
    id BIGSERIAL PRIMARY KEY,
    publication_id BIGINT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    destination_id BIGINT,
    destination_name VARCHAR(120) NOT NULL,
    destination_address VARCHAR(100) NOT NULL,
    status VARCHAR(50) NOT NULL,
    external_id VARCHAR(255),
    response_payload JSONB,
    published_at TIMESTAMP WITH TIME ZONE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_publication_targets_publication FOREIGN KEY (publication_id) REFERENCES publications (id) ON DELETE CASCADE,
    CONSTRAINT fk_publication_targets_destination FOREIGN KEY (destination_id) REFERENCES telegram_publication_destinations (id) ON DELETE SET NULL,
    CONSTRAINT ck_publication_targets_status CHECK (status IN ('PENDING', 'PUBLISHED', 'FAILED'))
);

CREATE TABLE publication_attachments
(
    id BIGSERIAL PRIMARY KEY,
    publication_id BIGINT NOT NULL,
    original_filename VARCHAR(255) NOT NULL,
    media_type VARCHAR(50) NOT NULL,
    mime_type VARCHAR(120) NOT NULL,
    file_size_bytes BIGINT NOT NULL,
    storage_path VARCHAR(500) NOT NULL,
    telegram_method VARCHAR(50) NOT NULL,
    position INTEGER NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_publication_attachments_publication FOREIGN KEY (publication_id) REFERENCES publications (id) ON DELETE CASCADE,
    CONSTRAINT ck_publication_attachments_size CHECK (file_size_bytes > 0),
    CONSTRAINT ck_publication_attachments_position CHECK (position >= 0)
);

CREATE TABLE ai_prompt_versions
(
    id BIGSERIAL PRIMARY KEY,
    prompt_key VARCHAR(80) NOT NULL,
    prompt_name VARCHAR(160) NOT NULL,
    module VARCHAR(80) NOT NULL,
    version VARCHAR(40) NOT NULL,
    checksum VARCHAR(64) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_ai_prompt_versions_key_version UNIQUE (prompt_key, version)
);

CREATE TABLE ai_operation_metrics
(
    id BIGSERIAL PRIMARY KEY,
    operation_type VARCHAR(80) NOT NULL,
    prompt_key VARCHAR(80) NOT NULL,
    provider VARCHAR(80) NOT NULL,
    model VARCHAR(120),
    status VARCHAR(30) NOT NULL,
    related_entity_type VARCHAR(80),
    related_entity_id BIGINT,
    latency_ms BIGINT NOT NULL,
    error_message VARCHAR(500),
    operation_details JSONB,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_ai_operation_metrics_status CHECK (status IN ('SUCCESS', 'FAILED')),
    CONSTRAINT ck_ai_operation_metrics_latency CHECK (latency_ms >= 0)
);

CREATE TABLE ai_provider_settings
(
    provider_code VARCHAR(50) PRIMARY KEY,
    display_name VARCHAR(120) NOT NULL,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    api_key_encrypted TEXT,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE ai_workflow_settings
(
    workflow_code VARCHAR(80) PRIMARY KEY,
    provider_code VARCHAR(50) NOT NULL,
    model_name VARCHAR(160) NOT NULL,
    temperature NUMERIC(4,3) NOT NULL DEFAULT 0.2,
    max_output_tokens INTEGER NOT NULL DEFAULT 1024,
    cooldown_seconds INTEGER NOT NULL DEFAULT 60,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_ai_workflow_settings_provider FOREIGN KEY (provider_code) REFERENCES ai_provider_settings (provider_code),
    CONSTRAINT ck_ai_workflow_settings_temperature CHECK (temperature >= 0 AND temperature <= 2),
    CONSTRAINT ck_ai_workflow_settings_max_tokens CHECK (max_output_tokens >= 1),
    CONSTRAINT ck_ai_workflow_settings_cooldown CHECK (cooldown_seconds >= 0)
);

CREATE INDEX idx_sources_active ON sources (active);
CREATE INDEX idx_sources_priority ON sources (priority);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_users_must_change_password ON users (must_change_password);
CREATE INDEX idx_users_status ON users (status);
CREATE INDEX idx_users_temporary_password_expires_at ON users (temporary_password_expires_at);

CREATE INDEX idx_user_password_history_user_id ON user_password_history (user_id);
CREATE INDEX idx_user_audit_log_user_id ON user_audit_log (user_id);
CREATE INDEX idx_user_audit_log_action ON user_audit_log (action);
CREATE INDEX idx_user_audit_log_created_at ON user_audit_log (created_at);

CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens (expires_at);
CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);
CREATE INDEX idx_refresh_tokens_active_user ON refresh_tokens (user_id, revoked_at, replaced_at);

CREATE INDEX idx_news_status ON news_articles (processing_status);
CREATE INDEX idx_news_published_at ON news_articles (published_at);
CREATE INDEX idx_news_source ON news_articles (source_id);
CREATE INDEX idx_news_hash ON news_articles (hash);
CREATE INDEX idx_news_articles_captured_visible ON news_articles (processing_status, captured_at DESC, id DESC);
CREATE INDEX idx_news_articles_updated_captured ON news_articles (updated_at DESC, captured_at DESC);

CREATE INDEX idx_classification_category ON news_classifications (category);
CREATE INDEX idx_classification_relevance ON news_classifications (relevance_score);

CREATE INDEX idx_event_status ON events (status);
CREATE INDEX idx_event_category ON events (category);
CREATE INDEX idx_event_importance ON events (importance);
CREATE INDEX idx_events_manual_discarded ON events (manual_discarded);
CREATE INDEX idx_events_dashboard_priority ON events (status, manual_discarded, category, importance, last_updated_at DESC, id DESC);
CREATE INDEX idx_events_first_detected ON events (status, first_detected_at DESC, id DESC);
CREATE INDEX idx_events_updated_last_updated ON events (updated_at DESC, last_updated_at DESC);

CREATE INDEX idx_event_news_event ON event_news (event_id);
CREATE INDEX idx_event_news_news ON event_news (news_id);
CREATE INDEX idx_event_news_match_decision ON event_news (match_decision);

CREATE INDEX idx_analysis_event ON event_ai_analysis (event_id);
CREATE INDEX idx_event_ai_analysis_event_generated ON event_ai_analysis (event_id, generated_at DESC, id DESC);
CREATE INDEX idx_event_ai_analysis_type_trigger ON event_ai_analysis (analysis_type, generation_trigger);

CREATE INDEX idx_content_event ON generated_content (event_id);
CREATE INDEX idx_content_status ON generated_content (status);
CREATE INDEX idx_content_channel ON generated_content (channel);
CREATE INDEX idx_generated_content_analysis_id ON generated_content (analysis_id);
CREATE INDEX idx_generated_content_event_status_generated ON generated_content (event_id, status, generated_at DESC, id DESC);
CREATE INDEX idx_generated_content_status_generated ON generated_content (status, generated_at DESC, id DESC);
CREATE INDEX idx_generated_content_active_generation ON generated_content (event_id, analysis_id, channel, content_type, status);

CREATE INDEX idx_publication_status ON publications (publication_status);
CREATE INDEX idx_publication_channel ON publications (channel);
CREATE INDEX idx_publications_content_status_published ON publications (content_id, publication_status, published_at DESC, id DESC);
CREATE INDEX idx_publications_status_scheduled ON publications (publication_status, scheduled_at DESC, id DESC);
CREATE INDEX idx_publications_published_at ON publications (published_at DESC);
CREATE INDEX idx_publications_type_status ON publications (publication_type, publication_status, id DESC);

CREATE INDEX idx_telegram_destinations_active ON telegram_publication_destinations (active, default_selected);
CREATE INDEX idx_publication_targets_publication ON publication_targets (publication_id, status);
CREATE INDEX idx_publication_attachments_publication ON publication_attachments (publication_id, position);

CREATE INDEX idx_audit_log_action ON audit_log (action);
CREATE INDEX idx_audit_log_entity ON audit_log (entity_type, entity_id);
CREATE INDEX idx_audit_log_created_at ON audit_log (created_at);
CREATE INDEX idx_audit_log_entity_action_created ON audit_log (entity_type, action, created_at DESC, id DESC);

CREATE INDEX idx_ai_prompt_versions_active ON ai_prompt_versions (active);
CREATE INDEX idx_ai_operation_metrics_created_at ON ai_operation_metrics (created_at DESC);
CREATE INDEX idx_ai_operation_metrics_operation_status ON ai_operation_metrics (operation_type, status);
CREATE INDEX idx_ai_operation_metrics_created_id ON ai_operation_metrics (created_at DESC, id DESC);
CREATE INDEX idx_ai_operation_metrics_entity_failures ON ai_operation_metrics (prompt_key, related_entity_type, related_entity_id, status, created_at DESC);
