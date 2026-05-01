## ADDED Requirements

### Requirement: Chat session history SHALL surface linked Agent trace summaries for session owners
The system SHALL allow a session owner to discover and open redacted Agent trace summaries associated with that session.

#### Scenario: Session owner opens linked trace summary
- **WHEN** a user opens a chat session that has linked Agent trace records
- **THEN** the system SHALL provide access to the redacted trace summary for that user's own session only
