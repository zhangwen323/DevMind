## Why

Agent execution traces already exist as an observability and audit requirement, but the product still lacks an operator-facing workflow to inspect failures and a user-facing way to understand what happened during agent-backed conversations. This makes troubleshooting slow, hides execution context behind raw storage, and leaves the frontend with an unfinished trace page.

## What Changes

- Add a read-only Agent trace operations view for system administrators
- Add a trace detail view with steps, tool calls, timing, status, and redacted input/output summaries
- Add a user-facing trace summary view reachable only from the owning chat session
- Add filtering by status, agent type, and keyword search for the administrator trace list
- Keep the entire change read-only; do not add retry, rerun, terminate, export, or global analytics

## Impact

- Adds a new `agent-trace-operations-view` capability to the main specs
- Updates `agent-execution` to require queryable trace records for operators and redacted summaries for end users
- Updates `chat-session-history` to require session-linked trace summary access for the owning user
- Adds backend APIs, permission checks, frontend pages, and verification coverage for trace inspection
