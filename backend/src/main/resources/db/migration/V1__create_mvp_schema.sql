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
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT uk_users_email UNIQUE (email)
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
    CONSTRAINT uk_news_articles_hash UNIQUE (hash)
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
    CONSTRAINT uk_news_classifications_news UNIQUE (news_id)
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
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW()
);

CREATE TABLE event_news
(
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    news_id BIGINT NOT NULL,
    confidence_score INTEGER,
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
    model_used VARCHAR(100) NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_event_ai_analysis_event FOREIGN KEY (event_id) REFERENCES events (id)
);

CREATE TABLE generated_content
(
    id BIGSERIAL PRIMARY KEY,
    event_id BIGINT NOT NULL,
    created_by BIGINT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    tone VARCHAR(50) NOT NULL,
    title VARCHAR(500) NOT NULL,
    content TEXT NOT NULL,
    status VARCHAR(50) NOT NULL,
    generated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    approved_at TIMESTAMP WITH TIME ZONE,
    CONSTRAINT fk_generated_content_event FOREIGN KEY (event_id) REFERENCES events (id),
    CONSTRAINT fk_generated_content_created_by FOREIGN KEY (created_by) REFERENCES users (id)
);

CREATE TABLE publications
(
    id BIGSERIAL PRIMARY KEY,
    content_id BIGINT NOT NULL,
    channel VARCHAR(50) NOT NULL,
    external_id VARCHAR(255),
    publication_status VARCHAR(50) NOT NULL,
    published_at TIMESTAMP WITH TIME ZONE,
    response_payload JSONB,
    CONSTRAINT fk_publications_content FOREIGN KEY (content_id) REFERENCES generated_content (id)
);

CREATE INDEX idx_sources_active ON sources (active);
CREATE INDEX idx_sources_priority ON sources (priority);

CREATE INDEX idx_news_status ON news_articles (processing_status);
CREATE INDEX idx_news_published_at ON news_articles (published_at);
CREATE INDEX idx_news_source ON news_articles (source_id);
CREATE INDEX idx_news_hash ON news_articles (hash);

CREATE INDEX idx_classification_category ON news_classifications (category);
CREATE INDEX idx_classification_relevance ON news_classifications (relevance_score);

CREATE INDEX idx_event_status ON events (status);
CREATE INDEX idx_event_category ON events (category);
CREATE INDEX idx_event_importance ON events (importance);

CREATE INDEX idx_event_news_event ON event_news (event_id);
CREATE INDEX idx_event_news_news ON event_news (news_id);

CREATE INDEX idx_analysis_event ON event_ai_analysis (event_id);

CREATE INDEX idx_content_event ON generated_content (event_id);
CREATE INDEX idx_content_status ON generated_content (status);
CREATE INDEX idx_content_channel ON generated_content (channel);

CREATE INDEX idx_publication_status ON publications (publication_status);
CREATE INDEX idx_publication_channel ON publications (channel);

CREATE INDEX idx_users_email ON users (email);
CREATE INDEX idx_users_role ON users (role);
CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens (expires_at);
