## ADDED Requirements

### Requirement: Model configuration SHALL support administrator-managed provider records
The system SHALL allow system administrators to create, list, view, and update global model-provider configuration records for the platform.

#### Scenario: System administrator creates a provider record
- **WHEN** a system administrator submits valid provider configuration data
- **THEN** the system SHALL persist a provider configuration record and expose it through administrative listing behavior

#### Scenario: Non-administrator attempts to manage provider records
- **WHEN** a user without system-administrator authority attempts to create or edit a provider configuration
- **THEN** the system SHALL reject the action

### Requirement: Model configuration SHALL persist only non-sensitive provider settings
The system SHALL persist non-sensitive provider settings, including environment-variable reference names, but SHALL NOT persist actual secret key values in model-configuration records.

#### Scenario: Provider configuration stores environment reference instead of raw key
- **WHEN** a system administrator saves provider credentials metadata
- **THEN** the system SHALL store only the configured environment-variable reference name and not the underlying secret value

### Requirement: Model configuration SHALL support enabled state and guarded default selection
The system SHALL support enabling or disabling provider records and SHALL allow chat-default and embedding-default selection only from enabled provider records.

#### Scenario: Enabled provider becomes default
- **WHEN** a system administrator marks an enabled provider as the default for chat or embedding
- **THEN** the system SHALL persist that default selection

#### Scenario: Disabled provider cannot become default
- **WHEN** a system administrator attempts to assign default chat or embedding status to a disabled provider
- **THEN** the system SHALL reject the change

#### Scenario: Current default provider cannot be disabled directly
- **WHEN** a system administrator attempts to disable a provider that is currently the default for chat or embedding
- **THEN** the system SHALL reject the disable operation until the default is moved elsewhere

### Requirement: Model configuration SHALL expose a current effective-configuration overview
The system SHALL provide a read-only overview of the currently effective chat and embedding provider defaults, including configured model names and environment-variable reference names.

#### Scenario: Administrator opens effective configuration overview
- **WHEN** a system administrator requests the model-configuration overview
- **THEN** the system SHALL return the current default chat provider, default chat model, default embedding provider, default embedding model, configured base URL values, and configured environment-variable reference names

### Requirement: Model configuration SHALL provide an administrative frontend workflow
The frontend SHALL provide a current-effective-configuration overview, a provider-configuration list, and create/edit/default-state management workflows for system administrators.

#### Scenario: Administrator manages provider configuration from the web console
- **WHEN** a system administrator opens the configuration console
- **THEN** the frontend SHALL display the effective configuration overview and allow listing, creating, editing, enabling, disabling, and default selection for provider records
