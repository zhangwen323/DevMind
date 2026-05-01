## 1. Backend Session Data Model

- [x] 1.1 Extend chat session persistence for `sessionType`, required RAG knowledge-base binding, optional Agent task context, and hard-delete support
- [x] 1.2 Implement chat session and message repository queries for create, list, detail, append, search, type filter, and delete behaviors
- [x] 1.3 Enforce user-owned session access and session-type validation rules in backend services

## 2. Backend RAG And Agent Integration

- [x] 2.1 Update RAG APIs and services to create or append messages within persistent RAG sessions
- [x] 2.2 Update Agent APIs and services to create or append messages within persistent Agent sessions with optional task context
- [x] 2.3 Add session history CRUD APIs for list, detail, continuation, and hard deletion

## 3. Frontend Session Workflow

- [x] 3.1 Add frontend session API client methods and Pinia state for unified conversation history
- [x] 3.2 Build session list UI with title search, `sessionType` filter, recent-first ordering, and delete action
- [x] 3.3 Update conversation views so users can open a prior session and continue sending messages
- [x] 3.4 Reflect RAG knowledge-base binding and Agent context handling in session creation and continuation flows

## 4. Verification And OpenSpec Consistency

- [x] 4.1 Add or update backend tests for session ownership, search, type filter, continuation, RAG binding validation, Agent context support, and hard delete
- [x] 4.2 Add or update frontend tests for session list filtering, detail loading, continuation, and deletion
- [x] 4.3 Run backend and frontend verification commands and fix any regressions
- [x] 4.4 Update OpenSpec artifacts if implementation reveals necessary scope or behavior adjustments before completion
