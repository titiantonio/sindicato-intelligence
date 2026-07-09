ALTER TABLE publication_targets
    DROP CONSTRAINT fk_publication_targets_destination,
    ADD CONSTRAINT fk_publication_targets_destination FOREIGN KEY (destination_id) REFERENCES telegram_publication_destinations (id) ON DELETE SET NULL;
