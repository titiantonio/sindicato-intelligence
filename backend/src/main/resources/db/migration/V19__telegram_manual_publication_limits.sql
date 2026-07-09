ALTER TABLE telegram_publication_settings
    ADD COLUMN max_attachment_count INTEGER NOT NULL DEFAULT 10,
    ADD COLUMN max_attachment_file_bytes BIGINT NOT NULL DEFAULT 20971520,
    ADD COLUMN max_attachment_total_bytes BIGINT NOT NULL DEFAULT 52428800;

ALTER TABLE telegram_publication_settings
    ADD CONSTRAINT ck_telegram_publication_settings_max_attachment_count CHECK (max_attachment_count > 0),
    ADD CONSTRAINT ck_telegram_publication_settings_max_attachment_file_bytes CHECK (max_attachment_file_bytes > 0),
    ADD CONSTRAINT ck_telegram_publication_settings_max_attachment_total_bytes CHECK (max_attachment_total_bytes > 0),
    ADD CONSTRAINT ck_telegram_publication_settings_max_attachment_total_ge_file CHECK (max_attachment_total_bytes >= max_attachment_file_bytes);
