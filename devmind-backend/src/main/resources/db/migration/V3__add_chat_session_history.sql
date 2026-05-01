ALTER TABLE chat_sessions
    ADD COLUMN session_type VARCHAR(50) NOT NULL DEFAULT 'RAG';

ALTER TABLE chat_sessions
    ADD COLUMN context_type VARCHAR(100);

ALTER TABLE chat_sessions
    ADD COLUMN context_id VARCHAR(255);
