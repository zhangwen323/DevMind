## 1. Repository Bootstrap

- [x] 1.1 Create the top-level module structure for `devmind-backend`, `devmind-ai-worker`, `devmind-frontend`, and `deploy`
- [x] 1.2 Add baseline build and package manager files for Maven, `uv`, and `pnpm`
- [x] 1.3 Add shared environment-variable examples and non-sensitive default configuration files for each module
- [x] 1.4 Add initial README or bootstrap notes that point contributors to OpenSpec and module entry points

## 2. Backend Foundation

- [x] 2.1 Scaffold the Spring Boot application with Java 17, Maven, Spring Security, Bean Validation, Flyway, SpringDoc OpenAPI, MyBatis, and MyBatis-Plus
- [x] 2.2 Implement unified API response primitives and global exception handling with layered business exceptions
- [x] 2.3 Add baseline authentication and authorization structure for role-based backend enforcement
- [x] 2.4 Add backend logging, configuration loading, and foundational test setup with JUnit 5 and Mockito

## 3. Data and Infrastructure Foundation

- [x] 3.1 Create initial Flyway migrations for core relational entities such as users, knowledge bases, documents, chat sessions, messages, and agent traces
- [x] 3.2 Define Redis key conventions for caching, rate limiting, session/state support, and distributed locks
- [x] 3.3 Define RocketMQ topics or queues for document ingestion and other long-running async workflows
- [x] 3.4 Define MinIO bucket and object-key conventions plus Qdrant collection or payload boundaries under OpenSpec-governed rules

## 4. AI Worker and Provider Foundation

- [x] 4.1 Scaffold the FastAPI worker with `uv`, structured logging, and `pytest`
- [x] 4.2 Implement the unified provider abstraction for Qwen and OpenAI across chat and embedding workloads
- [x] 4.3 Add bounded tool-execution interfaces that support observability, replay, and permission control
- [x] 4.4 Add worker-side trace and status primitives for ingestion, retrieval, and agent execution flows

## 5. Capability Implementation Baseline

- [x] 5.1 Implement the document upload and asynchronous ingestion skeleton with persisted metadata and tracked statuses
- [x] 5.2 Implement the initial RAG retrieval flow with knowledge-base isolation, document-state filtering, and traceable citations
- [x] 5.3 Implement the initial agent routing and trace model with bounded tool invocation
- [x] 5.4 Implement the initial SQL analysis flow with read-only safety enforcement and user-facing explanations

## 6. Frontend Foundation

- [x] 6.1 Scaffold the Vue application with Vite, Element Plus, `@element-plus/icons-vue`, Vue Router, Pinia, Axios, SCSS, ESLint, Prettier, Vitest, and Vue Test Utils
- [x] 6.2 Add authentication, routing, and layout foundations that respect backend-authoritative authorization
- [x] 6.3 Add baseline pages for login, knowledge-base management, document status tracking, chat, and agent trace visibility
- [x] 6.4 Add frontend test and lint commands to verify the bootstrap baseline

## 7. Governance and Follow-up Spec Work

- [x] 7.1 Add OpenSpec follow-up details for file-upload whitelist values, size limits, and storage-key templates
- [x] 7.2 Add OpenSpec follow-up decisions for observability tooling and rollout thresholds
- [x] 7.3 Verify that AGENTS guidance, foundation specs, and bootstrap implementation remain aligned before declaring the foundation complete
