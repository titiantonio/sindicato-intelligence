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

INSERT INTO telegram_publication_destinations (
    settings_id,
    name,
    chat_id,
    active,
    default_selected
)
SELECT
    id,
    'Principal',
    chat_id,
    TRUE,
    TRUE
FROM telegram_publication_settings
WHERE chat_id IS NOT NULL
  AND chat_id <> '';

ALTER TABLE publications
    ALTER COLUMN content_id DROP NOT NULL,
    ADD COLUMN publication_type VARCHAR(50) NOT NULL DEFAULT 'GENERATED_CONTENT',
    ADD COLUMN title_snapshot VARCHAR(500),
    ADD COLUMN message_snapshot TEXT,
    ADD COLUMN requested_by BIGINT;

ALTER TABLE publications
    ADD CONSTRAINT fk_publications_requested_by FOREIGN KEY (requested_by) REFERENCES users (id),
    ADD CONSTRAINT ck_publications_type CHECK (publication_type IN ('GENERATED_CONTENT', 'MANUAL_MESSAGE')),
    ADD CONSTRAINT ck_publications_content_or_manual CHECK (
        (publication_type = 'GENERATED_CONTENT' AND content_id IS NOT NULL)
        OR
        (publication_type = 'MANUAL_MESSAGE' AND content_id IS NULL)
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
    CONSTRAINT fk_publication_targets_destination FOREIGN KEY (destination_id) REFERENCES telegram_publication_destinations (id),
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

CREATE INDEX idx_telegram_destinations_active ON telegram_publication_destinations (active, default_selected);
CREATE INDEX idx_publications_type_status ON publications (publication_type, publication_status, id DESC);
CREATE INDEX idx_publication_targets_publication ON publication_targets (publication_id, status);
CREATE INDEX idx_publication_attachments_publication ON publication_attachments (publication_id, position);
