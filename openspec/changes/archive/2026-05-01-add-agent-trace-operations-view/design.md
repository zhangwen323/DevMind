## Overview

This change adds a two-level Agent trace inspection workflow:

- system administrators get a global, read-only trace list and full trace detail view
- ordinary users get a session-linked, redacted execution summary only for traces associated with their own conversations

The design deliberately separates operator visibility from user-facing explainability. Administrators can inspect the full execution chain needed for debugging. Users can understand whether their request succeeded, which agent handled it, the high-level execution path, and user-safe failure information without seeing internal prompts, full tool inputs, or internal stack details.

## Data Model And Visibility

The existing trace storage remains the source of truth. This change standardizes the response model exposed by the application:

- trace summary rows for the global list
- full trace detail for administrator-only inspection
- redacted trace summary for session-linked user inspection

The backend shall treat these as separate projections over the same persisted trace records. A trace remains associated with:

- `sessionId`
- `userId`
- `agentName`
- `stepName`
- `toolName`
- `status`
- timing fields
- serialized input/output and error payloads

User-visible responses must return only redacted or summarized fields. Internal trace payloads stay available only to system administrators.

## Backend API Shape

Administrator APIs:

- paginated global trace list
- filter by `status`
- filter by `agentName`
- keyword search across safe summary fields
- full trace detail by trace identifier

User API:

- session-linked trace summary list or detail endpoint scoped to the authenticated owner

The backend will enforce:

- only system administrators may access global trace search and full details
- ordinary users may access only trace summaries attached to their own chat sessions
- no mutation endpoints are added in this change

## Frontend Workflow

Administrator workflow:

- open the global trace page
- search and filter traces
- inspect a selected trace in a detail view

User workflow:

- open an owned chat session
- follow a trace summary link from the session context
- view a redacted execution summary

The existing `AgentTracePage.vue` becomes the administrator-facing global page. Chat session views gain a trace summary entry point for linked Agent runs.

## Redaction Rules

User-facing trace summaries must show:

- agent name
- success or failure state
- ordered high-level execution steps
- user-safe failure reason
- total duration
- timestamps

User-facing trace summaries must not show:

- full tool input parameters
- internal prompt assembly
- raw stack traces
- internal-only metadata
- any trace belonging to a different user

## Testing

Backend tests should cover:

- admin-only access to global trace list and full detail
- user denial for global trace endpoints
- owner-only access to session-linked summaries
- filter and keyword search behavior
- redaction of user-facing summaries

Frontend tests should cover:

- administrator list filtering and detail loading
- chat-session linked trace summary display
- user-visible redaction behavior at the view-model level

## Out Of Scope

This change does not include:

- retry
- rerun
- terminate
- export
- aggregate dashboards
- advanced time-range analytics
