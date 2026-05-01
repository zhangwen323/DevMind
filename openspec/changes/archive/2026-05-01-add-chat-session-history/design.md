## Context

The repository already contains foundational tables for `chat_sessions` and `chat_messages`, but there is no finished session-history capability, no searchable session management flow, and no consistent way for RAG or Agent interactions to append to an existing conversation. This change must connect existing knowledge-base management, RAG retrieval, agent execution, and user isolation into a unified conversation model while keeping the scope intentionally limited to the basic session lifecycle.

## Goals / Non-Goals

**Goals:**
- Introduce a unified conversation history model shared by `RAG` and `AGENT` interactions.
- Add user-scoped session listing with title search, `sessionType` filtering, and most-recent-first ordering.
- Add session detail, session continuation, and hard-delete behavior.
- Require RAG sessions to carry a knowledge-base binding and allow Agent sessions to carry optional knowledge-base and task-context binding.
- Expose the session lifecycle through backend APIs and frontend conversation management flows.

**Non-Goals:**
- Session sharing, favorites, pinning, or renaming.
- Cross-session search or advanced filters beyond title and session type.
- Message editing, retry-from-message, or branching conversation trees.
- Multi-user session collaboration.
- Changes to RAG ranking strategy or broader agent orchestration design beyond session persistence.

## Decisions

### Decision: Use one session model with explicit `sessionType`
RAG and Agent conversations will share one `chat_sessions` / `chat_messages` model, distinguished by a required `sessionType` value. This avoids parallel history systems and keeps user-facing history coherent.

Alternatives considered:
- Separate RAG and Agent history models: rejected because it would duplicate persistence and UI concerns.
- Single session model with implicit type inferred from messages: rejected because filtering and validation would be ambiguous.

### Decision: Make RAG knowledge-base binding mandatory and Agent binding optional
RAG sessions will require `knowledgeBaseId` so every continued follow-up preserves retrieval scope. Agent sessions will allow `knowledgeBaseId` to be null while supporting optional `contextType` and `contextId` fields for task-level continuity.

Alternatives considered:
- Make knowledge-base binding optional for all sessions: rejected because RAG scope would become ambiguous.
- Require knowledge-base binding for Agent sessions too: rejected because many agent tasks are not knowledge-base-centric.

### Decision: Support continuation through append-only messages
Continuing a conversation will append new user and assistant messages to the existing session rather than replacing history or creating implicit forks. This keeps persistence simple and matches the requested product behavior.

Alternatives considered:
- Read-only history with new sessions for follow-ups: rejected because it breaks conversational continuity.
- Editable or branchable history: rejected because it significantly expands scope.

### Decision: Hard-delete sessions and messages together
Deleting a session will physically remove the session and its messages from normal persistence rather than soft-deleting them. This matches the requested behavior and keeps list queries simpler for this phase.

Alternatives considered:
- Soft delete: rejected because the requested product behavior is hard deletion.
- Delete only session metadata and keep messages orphaned: rejected because it violates data integrity.

### Decision: Keep search/filter surface small but useful
The session list will support title search and `sessionType` filtering only, with default ordering by `updatedAt desc`. This is enough to make unified history usable without turning the change into a full chat-product management feature.

Alternatives considered:
- No filters beyond sorting: rejected because mixed RAG/Agent history becomes hard to navigate.
- Add knowledge-base/date/status filters now: rejected because they add complexity with lower initial payoff.

## Risks / Trade-offs

- [Appending messages to live sessions may require request-contract updates in multiple features] → Keep session-aware request fields minimal and update only RAG and Agent flows touched by this change.
- [Hard delete removes history permanently] → Keep delete explicit in UI and require user-owned access checks before removal.
- [Unified session list can still grow noisy] → Include title search and session-type filtering in the first version to preserve usability.
- [Agent context modeling can sprawl if over-designed early] → Limit context metadata to lightweight `contextType` / `contextId` fields and defer richer task-linking to later changes.
