ALTER TABLE knowledge_bases
    ADD COLUMN deleted BOOLEAN NOT NULL DEFAULT FALSE;

CREATE TABLE knowledge_base_permissions (
    knowledge_base_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (knowledge_base_id, user_id)
);
