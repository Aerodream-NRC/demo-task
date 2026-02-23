--liquibase formatted sql
--changeset author:2

CREATE TABLE history (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL,
    author VARCHAR(255) NOT NULL,
    action VARCHAR(20) NOT NULL,
    description TEXT,
    happend_at TIMESTAMP NOT NULL,
    CONSTRAINT fk_history_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE
);

CREATE INDEX idx_history_document_id ON history(document_id);