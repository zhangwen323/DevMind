## ADDED Requirements

### Requirement: Chat session history SHALL manage unified user-owned conversation sessions
The system SHALL manage conversation history through a unified session model shared by `RAG` and `AGENT` interactions, with each session owned and isolated by the authenticated user.

#### Scenario: User sees only own sessions
- **WHEN** a user requests the session history list
- **THEN** the system SHALL return only sessions owned by that user

### Requirement: Chat session history SHALL support searchable and filterable session lists
The system SHALL provide session listing with title search, `sessionType` filtering, and default ordering by most recently updated sessions first.

#### Scenario: Title search narrows sessions
- **WHEN** a user supplies a session-title search term
- **THEN** the system SHALL filter the session list to matching sessions owned by that user

#### Scenario: Session type filter isolates history category
- **WHEN** a user filters the session list by `RAG` or `AGENT`
- **THEN** the system SHALL return only sessions of the selected type owned by that user

### Requirement: Chat session history SHALL support session detail and message replay
The system SHALL return session metadata and ordered message history so a user can review and continue a previous conversation.

#### Scenario: User opens a prior session
- **WHEN** a user requests a session detail view for an owned session
- **THEN** the system SHALL return the session metadata and ordered messages for that session

### Requirement: Chat session history SHALL support append-only continuation
The system SHALL allow a user to continue an existing session by appending new messages to that session rather than creating a separate implicit conversation.

#### Scenario: Follow-up message extends prior session
- **WHEN** a user sends a new prompt within an existing session
- **THEN** the system SHALL persist the new message sequence under that same session and update the session timestamp

### Requirement: Chat session history SHALL hard-delete sessions and messages together
The system SHALL permanently remove a session and its associated messages when the owning user or authorized backend flow deletes that session.

#### Scenario: Deleted session no longer appears
- **WHEN** a user deletes an owned session
- **THEN** the system SHALL remove the session and its messages so they no longer appear in list or detail responses

### Requirement: Chat session history SHALL validate session-type bindings
The system SHALL require `knowledgeBaseId` for `RAG` sessions and SHALL allow `AGENT` sessions to omit `knowledgeBaseId` while optionally carrying task-context metadata.

#### Scenario: RAG session requires knowledge-base scope
- **WHEN** a client attempts to create or continue a `RAG` session without a `knowledgeBaseId`
- **THEN** the system SHALL reject the request

#### Scenario: Agent session may use task context without knowledge base
- **WHEN** a client creates or continues an `AGENT` session with task-context metadata and no `knowledgeBaseId`
- **THEN** the system SHALL accept the session if all other validation passes
