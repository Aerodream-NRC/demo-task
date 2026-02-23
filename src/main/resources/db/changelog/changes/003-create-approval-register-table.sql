--liquibase formatted sql
--changeset author:3
CREATE TABLE approve (
    id BIGSERIAL PRIMARY KEY,
    document_id BIGINT NOT NULL UNIQUE,
    approved_at TIMESTAMP NOT NULL,
    author VARCHAR(255) NOT NULL,
    CONSTRAINT fk_approval_document FOREIGN KEY (document_id) REFERENCES document(id) ON DELETE CASCADE
);