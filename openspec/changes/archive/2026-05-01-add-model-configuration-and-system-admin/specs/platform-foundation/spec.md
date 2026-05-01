## ADDED Requirements

### Requirement: Platform foundation SHALL separate model defaults from secret material
The platform SHALL manage non-sensitive provider defaults through system configuration while continuing to source actual provider secrets from environment variables.

#### Scenario: Runtime resolves provider secret through configured environment reference
- **WHEN** the platform needs credentials for the active provider configuration
- **THEN** the system SHALL resolve the actual secret from the configured environment-variable reference instead of from persisted model-configuration data
