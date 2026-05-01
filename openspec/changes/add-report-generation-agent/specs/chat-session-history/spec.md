## ADDED Requirements

### Requirement: Chat session history SHALL preserve report-generation conversations in Agent sessions
The system SHALL preserve report-generation interactions as part of the existing `AGENT` session history so users can revisit prior report work.

#### Scenario: User revisits prior report-generation conversation
- **WHEN** a user opens an `AGENT` session created by a report-generation request
- **THEN** the system SHALL return the prior report-generation conversation history for continued use
