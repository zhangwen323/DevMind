CREATE TABLE reports (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(255) NOT NULL,
    report_type VARCHAR(100) NOT NULL,
    knowledge_base_id BIGINT NOT NULL,
    session_id BIGINT,
    trace_key VARCHAR(100),
    guidance TEXT,
    content TEXT NOT NULL,
    citations_json TEXT NOT NULL,
    created_by BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE report_source_documents (
    report_id BIGINT NOT NULL,
    document_id BIGINT NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (report_id, document_id)
);
