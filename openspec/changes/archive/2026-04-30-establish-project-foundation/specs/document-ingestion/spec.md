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
