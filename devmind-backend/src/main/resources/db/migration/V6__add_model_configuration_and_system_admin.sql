CREATE TABLE model_provider_configs (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    provider_code VARCHAR(100) NOT NULL,
    display_name VARCHAR(150) NOT NULL,
    base_url VARCHAR(500),
    api_key_env_name VARCHAR(150) NOT NULL,
    chat_model VARCHAR(150) NOT NULL,
    embedding_model VARCHAR(150) NOT NULL,
    timeout_ms INT NOT NULL DEFAULT 30000,
    temperature DECIMAL(4,2) NOT NULL DEFAULT 0.20,
    notes TEXT,
    enabled BOOLEAN NOT NULL DEFAULT TRUE,
    default_chat BOOLEAN NOT NULL DEFAULT FALSE,
    default_embedding BOOLEAN NOT NULL DEFAULT FALSE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE model_config_audit_events (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    actor_user_id BIGINT NOT NULL,
    provider_config_id BIGINT,
    action_code VARCHAR(100) NOT NULL,
    outcome VARCHAR(50) NOT NULL,
    details_json TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
);
