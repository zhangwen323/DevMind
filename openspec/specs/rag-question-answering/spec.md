## ADDED Requirements

### Requirement: RAG question answering SHALL isolate retrieval by knowledge-base scope
The system SHALL restrict retrieval to the explicitly selected knowledge-base scope and SHALL not mix results across unauthorized or unrelated knowledge bases.

#### Scenario: Retrieval stays within selected knowledge base
- **WHEN** a user asks a question against a specific knowledge base
- **THEN** the retrieval stage SHALL search only documents that belong to that knowledge base and are visible to that user

### Requirement: RAG question answering SHALL filter by document readiness
The system SHALL exclude documents that are not in a retrieval-ready state from answer generation.

#### Scenario: Incomplete document is ignored during retrieval
- **WHEN** a document has not completed ingestion
- **THEN** the retrieval stage SHALL not use that document as answer evidence

### Requirement: RAG question answering SHALL return traceable citations
The system SHALL return answer citations that identify the underlying source material used to support the response.

#### Scenario: Answer includes attributable evidence
- **WHEN** the system generates an answer from retrieved materials
- **THEN** the response SHALL include references that allow the source document or segment to be traced

### Requirement: RAG question answering SHALL favor grounded behavior over unsupported synthesis
The system SHALL prefer grounded responses based on available evidence and SHALL communicate insufficient evidence when retrieval does not support a confident answer.

#### Scenario: Missing support results in constrained answer
- **WHEN** retrieval does not provide adequate support for the user question
- **THEN** the system SHALL avoid presenting an unsupported answer as established fact

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
