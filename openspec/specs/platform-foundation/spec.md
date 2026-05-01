## ADDED Requirements

### Requirement: Platform foundation SHALL define canonical module boundaries
The system foundation SHALL define separate backend, AI worker, frontend, and deployment modules with responsibilities that do not overlap across synchronous APIs, async AI processing, web UI delivery, and runtime environment assets.

#### Scenario: Repository bootstrap follows planned module layout
- **WHEN** implementation scaffolding is created from the foundation change
- **THEN** the repository SHALL contain distinct module roots for backend, AI worker, frontend, and deploy assets

### Requirement: Platform foundation SHALL govern engineering conventions
The platform SHALL standardize the confirmed baseline stack, coding conventions, testing expectations, and OpenSpec-first workflow rules needed for implementation.

#### Scenario: Contributor guidance matches foundation rules
- **WHEN** a contributor reads repository guidance and active foundation specs
- **THEN** the described stack, workflow constraints, and task governance SHALL be consistent with the foundation requirements

### Requirement: Platform foundation SHALL keep implementation details under OpenSpec control
Any change affecting behavior, interfaces, data structures, upload rules, or task boundaries SHALL update the corresponding OpenSpec artifacts in the same flow.

#### Scenario: Behavior-changing work requires spec updates
- **WHEN** a future change modifies an API contract or capability behavior
- **THEN** the implementation SHALL include matching OpenSpec artifact updates before completion is declared

### Requirement: Platform foundation SHALL separate model defaults from secret material
The platform SHALL manage non-sensitive provider defaults through system configuration while continuing to source actual provider secrets from environment variables.

#### Scenario: Runtime resolves provider secret through configured environment reference
- **WHEN** the platform needs credentials for the active provider configuration
- **THEN** the system SHALL resolve the actual secret from the configured environment-variable reference instead of from persisted model-configuration data
