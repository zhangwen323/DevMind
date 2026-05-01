## ADDED Requirements

### Requirement: Agent execution SHALL participate in persistent session history
The system SHALL create or append Agent interactions within a persistent user-owned session so prior task-oriented conversations can be reviewed and continued.

#### Scenario: Agent follow-up appends to existing session
- **WHEN** a user continues a prior Agent conversation
- **THEN** the system SHALL append the new user and assistant messages to that existing Agent session

### Requirement: Agent execution SHALL allow optional task context linkage per session
The system SHALL allow Agent sessions to carry optional task-context metadata even when no knowledge base is bound.

#### Scenario: Agent session carries task context without knowledge base
- **WHEN** a user creates or continues an Agent session with `contextType` and `contextId` but no `knowledgeBaseId`
- **THEN** the system SHALL preserve that task-context metadata for the session
