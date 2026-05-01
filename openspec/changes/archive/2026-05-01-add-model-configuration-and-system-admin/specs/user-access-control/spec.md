## ADDED Requirements

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
