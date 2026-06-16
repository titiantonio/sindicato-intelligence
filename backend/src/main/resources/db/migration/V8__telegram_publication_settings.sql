CREATE TABLE telegram_publication_settings
(
    id SMALLINT PRIMARY KEY,
    enabled BOOLEAN NOT NULL DEFAULT FALSE,
    base_url VARCHAR(255) NOT NULL,
    bot_token VARCHAR(255),
    chat_id VARCHAR(100),
    disable_web_page_preview BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    CONSTRAINT ck_telegram_publication_settings_singleton CHECK (id = 1),
    CONSTRAINT ck_telegram_publication_settings_base_url CHECK (base_url <> '')
);

INSERT INTO telegram_publication_settings (
    id,
    enabled,
    base_url,
    bot_token,
    chat_id,
    disable_web_page_preview
) VALUES (
    1,
    FALSE,
    'https://api.telegram.org',
    NULL,
    NULL,
    TRUE
);
