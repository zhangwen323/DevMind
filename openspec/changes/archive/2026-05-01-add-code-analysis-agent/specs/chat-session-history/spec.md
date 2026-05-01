## ADDED Requirements

### Requirement: Chat session history SHALL preserve code-analysis conversations in Agent sessions
The system SHALL preserve code-analysis interactions as part of the existing `AGENT` session history so users can review and continue them later.

#### Scenario: User revisits prior code-analysis conversation
- **WHEN** a user opens an `AGENT` session created by a code-analysis request
- **THEN** the system SHALL return the prior code-analysis conversation history for continued use
