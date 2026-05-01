## ADDED Requirements

### Requirement: Agent execution SHALL support structured report-generation tasks
The system SHALL support a `report-generation` task path that accepts a knowledge-base-scoped document selection, a supported report type, and optional user guidance, and returns structured report output with citations.

#### Scenario: Agent executes a report-generation task
- **WHEN** a client submits an Agent request for report generation with a visible knowledge base and eligible selected documents
- **THEN** the system SHALL execute the `report-generation` task path and return structured report output with citations
