## Context

DevMind is currently a concept-first repository with an OpenSpec workspace and a contributor guide, but no canonical capability specs or implementation task list. The project is intended to deliver a Spring Boot backend, a Python AI worker, and a Vue frontend over MySQL, Redis, RocketMQ, MinIO, and Qdrant. The confirmed engineering constraints include OpenSpec-first task control, unified API responses, security-first access control, auditable AI execution, and a provider abstraction for Qwen / OpenAI.

This foundation change is cross-cutting because it establishes capability boundaries, architectural responsibilities, and implementation guardrails across every planned subsystem before runtime code is introduced.

## Goals / Non-Goals

**Goals:**
- Define the initial capability contracts that future implementation must satisfy.
- Lock the baseline stack and module boundaries needed for consistent bootstrap work.
- Capture cross-cutting engineering rules for security, async processing, AI provider access, and traceability.
- Produce canonical implementation tasks for repository bootstrap and MVP delivery planning.

**Non-Goals:**
- Implement runnable backend, frontend, or worker code.
- Finalize every operational threshold such as file-size limits, observability tooling, or production scaling parameters.
- Specify detailed API field schemas beyond the requirement that they be governed by OpenSpec.

## Decisions

### Decision: Use OpenSpec as the only durable planning and task system
- **Why:** The repository already mandates OpenSpec-first execution and canonical task tracking in `openspec/changes/<change-name>/tasks.md`. Making OpenSpec the only durable source prevents divergence between contributor guidance and implementation intent.
- **Alternatives considered:** Maintain plans in `docs/` or issue trackers. Rejected because it creates parallel sources of truth.

### Decision: Split the system into backend, AI worker, frontend, and deploy modules
- **Why:** The confirmed stack naturally separates synchronous business APIs, async AI/document processing, web UI concerns, and deployment assets. This keeps responsibilities clear and allows feature-oriented development inside each module.
- **Alternatives considered:** Monolithic single service or backend-only architecture. Rejected because document parsing, RAG, and agent execution have different runtime and dependency needs.

### Decision: Treat Redis and RocketMQ as bounded infrastructure, not primary business stores
- **Why:** Redis is reserved for caching, rate limiting, session/state support, and distributed locks. RocketMQ is reserved for async decoupling and long-running tasks. This prevents hidden business state from being trapped in transient systems.
- **Alternatives considered:** Use Redis for primary workflow state or MQ-driven synchronous result delivery. Rejected because it complicates consistency and debugging.

### Decision: Require provider abstraction for Qwen / OpenAI and governance-first AI execution
- **Why:** DevMind needs model flexibility without vendor lock-in. A provider abstraction protects business logic from SDK coupling, while auditable, replayable, permission-bounded tool execution reduces operational and security risk before multi-agent complexity expands.
- **Alternatives considered:** Direct SDK use in business modules or unrestricted tool orchestration. Rejected because it weakens maintainability and control.

### Decision: Prioritize isolation, filtering, and citations over retrieval tuning
- **Why:** In a knowledge-system product, wrong-scope retrieval and uncited answers are more damaging than imperfect recall. Knowledge-base isolation, document-status filtering, and traceable citations are therefore baseline requirements before ranking optimization.
- **Alternatives considered:** Optimize retrieval metrics first. Rejected because it can improve the wrong behavior.

### Decision: Keep unresolved thresholds and observability stack as explicit follow-up items
- **Why:** File limits, storage key details, and observability tooling need operational refinement, but foundation work should still codify where those values belong. OpenSpec is the correct home for those details when settled.
- **Alternatives considered:** Freeze placeholder values now. Rejected because they would appear authoritative without sufficient validation.

## Risks / Trade-offs

- **[Foundation specs may feel broad]** → Mitigation: split requirements by capability and keep scenarios narrowly testable.
- **[Unspecified thresholds can delay implementation details]** → Mitigation: make their OpenSpec ownership explicit and schedule follow-up tasks for them.
- **[Cross-cutting rules may be ignored during bootstrap]** → Mitigation: encode them in both specs and canonical tasks before code work starts.
- **[AI provider abstraction can slow early coding]** → Mitigation: accept small upfront cost to avoid vendor-coupled rewrites later.

## Migration Plan

1. Establish the canonical foundation specs and tasks in this change.
2. Bootstrap the repository structure and module scaffolds against those specs.
3. Implement MVP capabilities in follow-up changes or tasks under this foundation.
4. Add unresolved operational details, such as observability tooling and exact upload thresholds, through subsequent OpenSpec updates.

Rollback is low risk because this change adds specification artifacts only; reverting means removing or revising the foundation change before implementation begins.

## Open Questions

- Which observability stack should become the default after MVP?
- What exact file-type whitelist, file-size limits, and storage-key templates should govern document ingestion?
- Should model routing distinguish default provider behavior between chat, embedding, and future rerank workloads?

## Follow-up Operational Decisions

- **Document ingestion defaults:** the foundation bootstrap uses `pdf`, `md`, `txt`, `docx`, `json`, and `sql` as the default whitelist, a 20 MB single-file limit, and the object-key templates captured in the document-ingestion spec.
- **Observability rollout threshold:** lightweight structured logs and trace records are sufficient during local bootstrap, but Prometheus + Grafana SHALL be introduced before any shared integration environment or long-running deployment is treated as a supported target.
