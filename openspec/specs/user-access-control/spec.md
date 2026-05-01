## ADDED Requirements

### Requirement: User access control SHALL authenticate users through backend authority
The system SHALL authenticate users through backend-managed identity checks and SHALL treat backend authorization decisions as authoritative over any frontend visibility controls.

#### Scenario: Frontend hides action but backend still enforces authorization
- **WHEN** a user attempts to access a protected action without sufficient permission
- **THEN** the backend SHALL reject the action even if the frontend incorrectly exposes it

### Requirement: User access control SHALL use role-based authorization
The system SHALL authorize protected capabilities based on explicit roles and permission boundaries defined for platform users.

#### Scenario: Knowledge-base administrator accesses scoped management functions
- **WHEN** a knowledge-base administrator requests a management operation within allowed scope
- **THEN** the system SHALL permit the operation without granting unrestricted system-administrator access

### Requirement: User access control SHALL support auditable security events
The system SHALL retain auditable records for critical security-relevant actions including authentication events and protected administrative operations.

#### Scenario: Administrative change is recorded
- **WHEN** an authorized user performs a protected administrative change
- **THEN** the system SHALL retain an audit record identifying the actor, action, outcome, and execution time

### Requirement: User access control SHALL protect model-configuration administration
The system SHALL restrict model-configuration management operations to system administrators.

#### Scenario: System administrator accesses model configuration console
- **WHEN** a system administrator requests model-configuration management capabilities
- **THEN** the system SHALL permit the protected operations

#### Scenario: Ordinary user is denied model-configuration management
- **WHEN** an ordinary user requests a model-configuration management operation
- **THEN** the system SHALL reject the request

### Requirement: User access control SHALL audit protected model-configuration changes
The system SHALL retain auditable records for model-configuration create, update, enable/disable, and default-selection operations.

#### Scenario: Provider default is changed
- **WHEN** a system administrator changes the default chat or embedding provider
- **THEN** the system SHALL retain an audit record for the protected configuration change
