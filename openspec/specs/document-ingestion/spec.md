## ADDED Requirements

### Requirement: Document ingestion SHALL accept only governed uploads
The system SHALL enforce a file-type whitelist, upload-size limits, and storage-key rules defined in OpenSpec before accepting a document into the ingestion pipeline.

#### Scenario: Disallowed file type is rejected
- **WHEN** a user uploads a file outside the configured whitelist
- **THEN** the system SHALL reject the upload before scheduling ingestion work

#### Scenario: Foundation upload policy is applied
- **WHEN** the foundation bootstrap handles document uploads
- **THEN** the accepted extensions SHALL be `pdf`, `md`, `txt`, `docx`, `json`, and `sql`
- **THEN** the default single-file size limit SHALL be 20 MB
- **THEN** the default object-key template for source files SHALL be `<env>/kb/<kbId>/documents/<docId>/source/<fileName>`
- **THEN** the default object-key template for derived artifacts SHALL be `<env>/kb/<kbId>/documents/<docId>/derived/<artifactName>`

### Requirement: Document ingestion SHALL persist metadata before asynchronous processing
The system SHALL store accepted document metadata and initial processing state before dispatching asynchronous ingestion work.

#### Scenario: Accepted upload enters tracked processing lifecycle
- **WHEN** a valid document upload is accepted
- **THEN** the system SHALL persist document metadata and a tracked initial status before publishing asynchronous processing work

### Requirement: Document ingestion SHALL process documents asynchronously
The system SHALL use asynchronous task execution for parsing, chunking, embedding, and vector indexing instead of synchronous request-response completion.

#### Scenario: Long-running ingestion work is decoupled from upload response
- **WHEN** a document requires parsing and embedding
- **THEN** the upload API SHALL return without waiting for final indexing completion

### Requirement: Document ingestion SHALL retain processing status and traceability
The system SHALL expose document processing status and retain audit or trace records for significant ingestion steps and failures.

#### Scenario: Failed parsing remains diagnosable
- **WHEN** a parsing or embedding step fails
- **THEN** the system SHALL retain a failure status and enough trace information to diagnose the failed step

### Requirement: Document ingestion SHALL expose knowledge-base-scoped document listing behavior
The system SHALL provide document listing behavior scoped to a single visible knowledge base so management views can inspect document status without crossing knowledge-base boundaries.

#### Scenario: Knowledge-base detail shows only its own documents
- **WHEN** a user requests the linked document list for a visible knowledge base
- **THEN** the system SHALL return only documents that belong to that knowledge base and are visible to that user

#### Scenario: Deleted knowledge base is not available for normal document management views
- **WHEN** a knowledge base has been soft-deleted
- **THEN** the system SHALL not expose its document list through standard knowledge-base management flows
