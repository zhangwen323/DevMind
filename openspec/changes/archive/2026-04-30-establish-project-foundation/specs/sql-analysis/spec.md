## ADDED Requirements

### Requirement: SQL analysis SHALL translate natural-language questions into governed read-only queries
The system SHALL translate supported natural-language analytical requests into SQL constrained to approved read-only behavior.

#### Scenario: Analytical question becomes safe query candidate
- **WHEN** a user asks a supported data-analysis question
- **THEN** the system SHALL produce a read-only SQL query candidate rather than an unrestricted statement

### Requirement: SQL analysis SHALL block unsafe query behavior
The system SHALL reject or constrain SQL that attempts disallowed operations, exceeds approved data scope, or violates safety rules such as read-only enforcement.

#### Scenario: Mutating SQL is prevented
- **WHEN** generated or requested SQL includes a mutating or destructive operation
- **THEN** the system SHALL block execution and report that the request violates SQL safety policy

### Requirement: SQL analysis SHALL return interpretable results
The system SHALL return executed SQL results together with a user-facing explanation of what the query represents.

#### Scenario: Successful SQL analysis includes explanation
- **WHEN** a SQL analysis request executes successfully
- **THEN** the response SHALL include both the query result and an explanation of the returned information
