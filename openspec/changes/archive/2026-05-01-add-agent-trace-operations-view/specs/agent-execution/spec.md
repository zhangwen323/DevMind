## ADDED Requirements

### Requirement: Agent execution SHALL expose queryable operator trace views
The system SHALL expose queryable trace records for system administrators so recent executions can be searched by status, agent name, and keyword and inspected in full detail.

#### Scenario: Administrator inspects failed execution
- **WHEN** a system administrator searches for failed traces
- **THEN** the system SHALL return matching executions with enough detail to investigate the failure

### Requirement: Agent execution SHALL expose redacted end-user execution summaries
The system SHALL expose a redacted execution summary for an ordinary user only when the trace is linked to that user's own session.

#### Scenario: User views linked Agent execution summary
- **WHEN** a user requests an Agent trace linked to one of that user's own sessions
- **THEN** the system SHALL return a user-safe summary without exposing internal-only execution details
