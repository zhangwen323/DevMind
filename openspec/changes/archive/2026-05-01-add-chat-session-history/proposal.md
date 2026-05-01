## Why

The platform already has separate foundations for RAG queries, agent execution, user access control, and knowledge-base management, but it does not yet provide a durable conversation model that users can revisit and continue. This change is needed now to turn one-off interactions into a usable product workflow with searchable session history, continued follow-up, and governed deletion behavior.

## What Changes

- Add unified chat-session history capability built on shared session and message models for both `RAG` and `AGENT` conversations.
- Add session list, detail, creation, continuation, and hard-delete behaviors with title search and session-type filtering.
- Require RAG sessions to bind to a knowledge base and allow Agent sessions to operate with optional knowledge-base binding plus task context metadata.
- Extend RAG and Agent interaction flows so new messages can be appended to an existing session instead of only creating isolated single-turn responses.
- Keep scope intentionally narrow for this change: no favorites, no renaming, no advanced filters beyond title and session type, no cross-session search, and no message edit/retry flows.

## Capabilities

### New Capabilities
- `chat-session-history`: Manage unified user-owned conversation sessions and messages across RAG and Agent flows, including creation, listing, continuation, and deletion.

### Modified Capabilities
- `rag-question-answering`: RAG interactions must participate in persistent session history and continue within a knowledge-base-bound session.
- `agent-execution`: Agent interactions must participate in persistent session history and continue within a typed session that may carry task context.

## Impact

- Affected backend areas: chat session/message persistence, RAG and Agent request models, session-aware services, search and filter queries, and delete semantics.
- Affected frontend areas: conversation list UI, chat/session detail views, session filters, continuation behavior, and deletion controls.
- Affected data model: `chat_sessions`, `chat_messages`, session type fields, knowledge-base linkage requirements, and optional agent context fields.
- Affected systems: MySQL persistence, user-scoped authorization checks, and OpenSpec specs for RAG and Agent behavior.
