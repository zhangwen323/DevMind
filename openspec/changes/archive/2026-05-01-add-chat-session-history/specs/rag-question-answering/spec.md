## ADDED Requirements

### Requirement: RAG question answering SHALL participate in persistent session history
The system SHALL create or append RAG interactions within a persistent user-owned session so prior RAG turns can be reviewed and continued.

#### Scenario: RAG follow-up appends to existing session
- **WHEN** a user continues a prior RAG conversation
- **THEN** the system SHALL append the new user and assistant messages to that existing RAG session

### Requirement: RAG question answering SHALL preserve knowledge-base binding across session continuation
The system SHALL ensure that a RAG session remains bound to a single knowledge base across subsequent turns.

#### Scenario: Continued RAG session keeps original knowledge-base scope
- **WHEN** a user sends a follow-up within an existing RAG session
- **THEN** the system SHALL use the session’s bound knowledge base as the retrieval scope
