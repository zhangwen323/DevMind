## ADDED Requirements

### Requirement: Agent execution SHALL support structured code-analysis tasks
The system SHALL support a `code-analysis` task path that accepts pasted code input and returns structured analysis sections through the Agent execution workflow.

#### Scenario: Agent routes code-analysis task
- **WHEN** a client submits an Agent request for code analysis
- **THEN** the system SHALL execute the `code-analysis` task path and return structured output
