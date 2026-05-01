## ADDED Requirements

### Requirement: Agent trace operations view SHALL provide a read-only global trace list for system administrators
The system SHALL provide system administrators with a read-only global trace list ordered by most recent execution first and filterable by `status`, `agentName`, and keyword search.

#### Scenario: Administrator filters trace list
- **WHEN** a system administrator opens the global trace list and applies `status`, `agentName`, or keyword filters
- **THEN** the system SHALL return only matching traces ordered by most recent execution first

### Requirement: Agent trace operations view SHALL provide full trace detail to system administrators
The system SHALL provide system administrators with full trace detail sufficient for debugging, including ordered steps, tool calls, summarized input/output payloads, timing, status, and error information.

#### Scenario: Administrator opens a trace detail
- **WHEN** a system administrator requests a specific trace detail
- **THEN** the system SHALL return the full operator-visible execution chain for that trace

### Requirement: Agent trace operations view SHALL provide session-linked redacted summaries to ordinary users
The system SHALL allow an authenticated ordinary user to view only redacted trace summaries linked to that user's own chat sessions.

#### Scenario: User opens trace summary from owned session
- **WHEN** a user opens a trace summary from a chat session owned by that user
- **THEN** the system SHALL return only the redacted, user-visible trace summary for that linked execution

### Requirement: Agent trace operations view SHALL deny unauthorized global access
The system SHALL deny global trace list and full trace detail access to non-administrator users.

#### Scenario: Non-administrator requests global trace list
- **WHEN** an authenticated non-administrator requests the global trace list
- **THEN** the system SHALL reject the request

### Requirement: Agent trace operations view SHALL remain read-only
The system SHALL not expose retry, rerun, terminate, export, or other mutating operator actions in this capability.

#### Scenario: Operator attempts mutation from trace view
- **WHEN** an operator uses the trace operations view
- **THEN** the system SHALL expose inspection-only workflows and no mutating execution controls
