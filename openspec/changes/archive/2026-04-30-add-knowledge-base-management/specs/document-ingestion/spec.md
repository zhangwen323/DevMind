## ADDED Requirements

### Requirement: Document ingestion SHALL expose knowledge-base-scoped document listing behavior
The system SHALL provide document listing behavior scoped to a single visible knowledge base so management views can inspect document status without crossing knowledge-base boundaries.

#### Scenario: Knowledge-base detail shows only its own documents
- **WHEN** a user requests the linked document list for a visible knowledge base
- **THEN** the system SHALL return only documents that belong to that knowledge base and are visible to that user

#### Scenario: Deleted knowledge base is not available for normal document management views
- **WHEN** a knowledge base has been soft-deleted
- **THEN** the system SHALL not expose its document list through standard knowledge-base management flows
