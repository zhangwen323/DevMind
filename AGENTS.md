# Repository Guidelines

## Project Structure & Module Organization
This repository is OpenSpec-first. Requirements and tasks live under `openspec/`, especially `openspec/changes/<change-name>/` and `openspec/specs/`. See `devmind_project_spec.md`.

Planned layout:
- `devmind-backend/` for Spring Boot APIs
- `devmind-ai-worker/` for Python + FastAPI AI and document processing
- `devmind-frontend/` for the Vue application
- `deploy/` for `docker-compose.yml`, Nginx, and bootstrap assets

## Stack & Rules
Backend: Spring Boot, Java 17, Maven, MyBatis + MyBatis-Plus, Flyway, SpringDoc OpenAPI, Bean Validation, unified responses, layered business exceptions, SLF4J + Logback, JUnit 5 + Mockito.  
AI worker: Python 3.11, FastAPI, `uv`, `logging`, `pytest`.  
Frontend: Vue + JavaScript, Element Plus, `@element-plus/icons-vue`, Vite, `pnpm`, Vue Router, Pinia, Axios, SCSS, Vitest, Vue Test Utils, ESLint, Prettier.  
Infra: MySQL, Redis, RocketMQ, MinIO, Qdrant, Docker Compose, Nginx, Spring Security + JWT.

Redis is only for caching, rate limiting, session/state support, and distributed locks. RocketMQ is only for async decoupling and long-running tasks, not synchronous result delivery. Sensitive config must use environment variables; file-upload rules and API field details belong in OpenSpec.

Use Qwen / OpenAI only through a unified provider abstraction. RAG must prioritize isolation, document-state filtering, and traceable citations before recall/ranking tuning. Agent tooling must be observable, replayable, permission-bounded, and auditable before multi-agent orchestration.

## Build, Test, and Development Commands
Until runnable modules exist, use OpenSpec commands:
- `openspec list --json`
- `openspec new change "<kebab-case-name>"`
- `openspec status --change "<name>" --json`

When modules are added, prefer `mvn test`, `mvn spring-boot:run`, `pytest`, and `docker compose up -d`.

## Coding Style & Testing
Use `kebab-case` for OpenSpec change names like `add-sql-agent`. Use 4-space indentation, `PascalCase` Java classes, `camelCase` Java methods, and `snake_case` Python modules/functions. Organize code by feature: `auth/`, `knowledge/`, `document/`, `agent/`.

Treat tests as part of every change. Place Java tests under `src/test/java/`, Python tests under `tests/`, and frontend tests in feature folders or `src/__tests__/`.

## PRs & OpenSpec Discipline
Use clear imperative commits, preferably Conventional Commit style such as `feat: add RAG retrieval task spec`.

PRs should include a short summary, the related OpenSpec change name, test evidence or a note if tests do not apply, and screenshots only for UI changes.

Do not create a parallel planning system outside `openspec/`. Treat `openspec/changes/<change-name>/tasks.md` as the canonical task list. Any change affecting behavior, interfaces, data structures, file-upload rules, or task boundaries must update the matching OpenSpec artifacts in the same flow.
