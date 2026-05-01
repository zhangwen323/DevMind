## ADDED Requirements

### Requirement: Code analysis agent SHALL analyze pasted code snippets
The system SHALL allow a user to submit a pasted code snippet for analysis without requiring repository access or file upload.

#### Scenario: User submits a pasted snippet
- **WHEN** a user submits a code snippet through the code-analysis workflow
- **THEN** the system SHALL accept the snippet for code-analysis handling

### Requirement: Code analysis agent SHALL support optional language input
The system SHALL allow a user to optionally specify the programming language, and SHALL permit analysis to continue when the language is omitted.

#### Scenario: User omits language
- **WHEN** a user submits a code snippet without a language value
- **THEN** the system SHALL continue analysis using automatic inference or default handling

### Requirement: Code analysis agent SHALL return structured analysis sections
The system SHALL return structured code-analysis output that separates overview, potential issues, optimization suggestions, and generated comments or explanation notes.

#### Scenario: User receives structured result
- **WHEN** a code-analysis request succeeds
- **THEN** the system SHALL return the analysis in structured sections rather than a single free-form paragraph

### Requirement: Code analysis agent SHALL remain within the existing Agent session model
The system SHALL persist code-analysis conversations within the existing `AGENT` session model rather than creating a new session type.

#### Scenario: Code analysis continues in existing Agent session
- **WHEN** a user continues a prior code-analysis conversation
- **THEN** the system SHALL append the interaction to that existing `AGENT` session
