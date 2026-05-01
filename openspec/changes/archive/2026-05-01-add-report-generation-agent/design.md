## Context

DevMind already has the main primitives needed for governed report generation: knowledge-base scoping, completed document ingestion, persistent `AGENT` sessions, and traceable Agent execution. The missing layer is a report-focused workflow that lets a user select a knowledge base, constrain the source documents, generate a structured report, and later reopen the saved result. This change crosses backend domain modeling, frontend workflows, and Agent task orchestration, so a design document is warranted.

## Goals / Non-Goals

**Goals:**
- Generate reports from a selected knowledge base and an explicit subset of its `COMPLETED` documents
- Support four report types: daily summary, weekly summary, project summary, and technical-solution draft
- Persist report records with body content, source citations, and creator metadata
- Reuse the existing `AGENT` session and trace model so report generation is auditable and resumable
- Provide frontend generation, listing, and detail workflows

**Non-Goals:**
- Editing generated reports after creation
- Exporting reports to Word, PDF, or external publishing systems
- Generating reports directly from free-form chat history as the primary input mode
- Multi-version report management or approval workflows

## Decisions

### 1. Report generation will use knowledge-base documents as the primary source of truth
The user must first choose a knowledge base and then select one or more `COMPLETED` documents from that knowledge base. This keeps report generation aligned with governed project knowledge and avoids treating transient chat content as the canonical source. The lighter alternative of generating from free-form notes was rejected because it weakens traceability and overlaps with generic LLM writing.

### 2. Reports will be persisted as first-class records
Generated reports will be stored independently from chat messages, with fields for type, title, selected document scope, generated content, citations, creator, and session linkage. This separates the durable output from the conversational process. Returning only an in-memory response was rejected because reports are meant to be revisited and managed over time.

### 3. Report generation will reuse the existing Agent execution pipeline
The system will introduce a `report-generation` task path under the existing Agent execution model instead of inventing a separate orchestration subsystem. This keeps session history, trace records, and audit behavior consistent across Agent capabilities. A standalone report engine was rejected because it would duplicate execution and observability patterns already present in the platform.

### 4. The frontend will expose a focused three-page workflow
The web console will provide:
- a report generation page for selecting a knowledge base, selecting documents, choosing a report type, and optionally supplying guidance
- a report list page for reviewing saved reports
- a report detail page for reading the full generated content and citations

This is preferred over embedding report generation into the generic chat page because the inputs and results are more structured and document-scoped than ordinary chat interactions.

## Risks / Trade-offs

- [Large source selections can degrade report quality] → Limit the scope to explicitly selected `COMPLETED` documents and keep the initial workflow manual rather than automatically sweeping the entire knowledge base.
- [Generated reports may look authoritative even when source coverage is thin] → Require citations in the persisted output and keep the selected source-document set visible in report details.
- [Persisting reports introduces more domain state] → Keep the first iteration simple: create, list, detail only; no editing, versioning, or workflow states beyond successful persistence.
- [The generic Agent session model may blur report-specific UX] → Expose dedicated report pages while still linking the generation run back to the underlying `AGENT` session and trace context.
