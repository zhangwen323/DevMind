## Context

The foundation change established platform modules, document ingestion, RAG retrieval, and role-based access control, but those capabilities are not yet connected through a first-class knowledge-base workflow. Users need a single-layer knowledge-base model that can be created, viewed, updated, and soft-deleted from both backend APIs and the frontend console. The design must preserve existing OpenSpec rules: backend authorization is authoritative, document ingestion remains asynchronous, RAG stays scoped by knowledge-base boundaries, and authorization assignment UI is out of scope for this change.

## Goals / Non-Goals

**Goals:**
- Introduce a `KnowledgeBase` domain model with backend CRUD, pagination, name search, detail retrieval, and soft deletion.
- Define authorization behavior so creators and system administrators can manage knowledge bases, while ordinary users can only access knowledge bases already authorized to them.
- Add a frontend management flow with list, create, edit, delete, and detail pages.
- Expose knowledge-base-scoped document listings so document status can be reviewed from the knowledge-base detail page.

**Non-Goals:**
- Workspace or project hierarchy above knowledge bases.
- Knowledge-base member assignment or sharing UI.
- Recycle-bin restore flows, bulk operations, or advanced filters beyond name search.
- Changes to asynchronous ingestion architecture, vector indexing design, or RAG ranking strategy.

## Decisions

### Decision: Use a single-layer knowledge-base aggregate
The system will model knowledge bases as a single top-level managed resource rather than introducing workspace nesting. This keeps the change aligned with current `kbId` boundaries already used in document ingestion and RAG retrieval.

Alternatives considered:
- Two-level workspace plus knowledge-base hierarchy: rejected because it would expand scope into multi-tenant structure, inherited permissions, and broader navigation changes.
- Document-centric management with no explicit knowledge-base aggregate: rejected because it would not provide a durable management boundary for authorization, ingestion, and retrieval.

### Decision: Implement soft deletion with default active-only listing
Knowledge bases will be soft-deleted so auditability and document traceability are preserved. Standard list and detail flows will treat deleted knowledge bases as unavailable unless a later change introduces recovery or administrative recycle-bin behavior.

Alternatives considered:
- Hard deletion: rejected because it would make audit and downstream document/index cleanup riskier.
- Exposed recycle-bin flows in this change: rejected because recovery operations are not required for the first management workflow.

### Decision: Keep authorization simple and backend-enforced
Management permissions will be limited to the creator and system administrators. Read visibility for ordinary users will be based on existing authorization data supplied by backend policy, without adding authorization-management UI in this change.

Alternatives considered:
- Full knowledge-base membership management now: rejected because it would broaden scope into an access-administration feature set.
- Frontend-only visibility enforcement: rejected because it conflicts with existing backend-authoritative access-control requirements.

### Decision: Deliver a full CRUD loop with a thin but real frontend
The change will include backend APIs and a basic frontend management experience rather than backend-only scaffolding. This allows the existing upload and retrieval capabilities to be exercised through a usable workflow while still keeping the UI intentionally narrow.

Alternatives considered:
- Backend-only delivery: rejected because it would delay validation of the actual management path.
- Rich management console with advanced filters, bulk actions, and analytics: rejected because it adds non-essential scope.

### Decision: Link document status through knowledge-base detail views
Knowledge-base detail pages will include document-list linkage backed by a knowledge-base-scoped document query API. This preserves the existing document lifecycle model while making ingestion status operationally visible from the management flow.

Alternatives considered:
- Separate standalone document screen with no knowledge-base linkage: rejected because it weakens the end-to-end management workflow.
- Embedding document upload redesign into this change: rejected because upload mechanics already belong to `document-ingestion`.

## Risks / Trade-offs

- [Authorization source for “already authorized” users is still coarse] → Use existing backend role/policy rules for now and keep membership-assignment UI explicitly out of scope until a separate change defines it.
- [Soft deletion leaves related documents and indexes intact] → Treat deleted knowledge bases as non-listable/non-manageable in normal flows and defer cleanup or restore semantics to a later change.
- [Frontend CRUD adds more implementation surface than backend-only work] → Keep the UI constrained to list, modal or form editing, detail view, and document linkage only.
- [Document-list linkage can blur boundaries between management and ingestion] → Reuse ingestion status data rather than redefining parsing or processing responsibilities in this change.
