## Why

DevMind already supports governed knowledge bases, document ingestion, RAG, Agent execution, and persistent chat history, but it still lacks a way to turn curated project knowledge into reusable written outputs. Report generation is the next natural capability because it converts stored team knowledge into durable artifacts such as daily summaries, weekly summaries, project summaries, and technical-solution drafts.

## What Changes

- Add a report-generation capability that creates reports from selected `COMPLETED` documents inside a chosen knowledge base.
- Persist generated reports as first-class records instead of returning only transient chat output.
- Provide report generation, report list, and report detail workflows in the frontend.
- Route report generation through the existing Agent execution stack with traceability and audit context.
- Reuse `AGENT` chat sessions so report-generation conversations remain reviewable and resumable.

## Capabilities

### New Capabilities
- `report-generation-agent`: Generate structured reports from selected knowledge-base documents, persist the generated report, and expose report creation, listing, and detail views.

### Modified Capabilities
- `agent-execution`: Add a `report-generation` task path that accepts governed report inputs and returns structured report output with citations.
- `chat-session-history`: Preserve report-generation interactions inside existing `AGENT` sessions so users can revisit prior report work.

## Impact

- Backend: new report domain model, persistence, APIs, and Agent orchestration branch
- Frontend: new report generation page, report list page, and report detail page
- Data: new report records tied to knowledge bases, selected documents, users, and session/trace context
- Agent flow: new report-generation task type, with persisted output and citations
