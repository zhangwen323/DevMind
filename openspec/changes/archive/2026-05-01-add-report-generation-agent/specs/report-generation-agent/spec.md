## ADDED Requirements

### Requirement: Report generation SHALL create structured reports from governed knowledge-base documents
The system SHALL allow a user to generate a structured report by selecting a visible knowledge base and one or more `COMPLETED` documents within that knowledge base.

#### Scenario: User generates a report from selected documents
- **WHEN** a user submits a report-generation request with a visible knowledge base, at least one selected `COMPLETED` document, and a supported report type
- **THEN** the system SHALL generate a structured report scoped only to the selected documents

#### Scenario: Ineligible document is rejected from the report scope
- **WHEN** a report-generation request includes a document outside the selected knowledge base or not in `COMPLETED` status
- **THEN** the system SHALL reject the request

### Requirement: Report generation SHALL support a bounded set of report types
The system SHALL support `daily-summary`, `weekly-summary`, `project-summary`, and `technical-solution-draft` as the initial report types.

#### Scenario: Supported report type is accepted
- **WHEN** a user submits one of the supported report types
- **THEN** the system SHALL process the request using that report template or mode

#### Scenario: Unsupported report type is rejected
- **WHEN** a user submits a report type outside the supported set
- **THEN** the system SHALL reject the request

### Requirement: Report generation SHALL persist report records for later viewing
The system SHALL persist each generated report as a first-class record containing its type, title, knowledge-base scope, selected document scope, generated content, citations, creator, and creation time.

#### Scenario: Generated report becomes a durable record
- **WHEN** a report is generated successfully
- **THEN** the system SHALL store the report as a durable record retrievable from report list and detail views

### Requirement: Report generation SHALL expose report list and detail views
The system SHALL provide APIs and frontend workflows to list saved reports and inspect a single report in detail.

#### Scenario: User opens the report list
- **WHEN** a user requests the saved reports view
- **THEN** the system SHALL return persisted reports visible to that user

#### Scenario: User opens a report detail view
- **WHEN** a user opens a visible report record
- **THEN** the system SHALL return the full generated content and source citations for that report

### Requirement: Report generation SHALL retain citation traceability
The system SHALL retain source citations so generated report sections remain traceable to the selected knowledge-base documents.

#### Scenario: Saved report shows source citations
- **WHEN** a user views a generated report detail
- **THEN** the system SHALL display citations tied to the selected source documents

### Requirement: Report generation SHALL provide a dedicated frontend workflow
The frontend SHALL provide a report generation page, a report list page, and a report detail page for the initial report lifecycle.

#### Scenario: User completes the report generation workflow
- **WHEN** a user selects a knowledge base, selects eligible documents, chooses a report type, and submits generation
- **THEN** the frontend SHALL create the report and expose the saved result through the report list and detail views
