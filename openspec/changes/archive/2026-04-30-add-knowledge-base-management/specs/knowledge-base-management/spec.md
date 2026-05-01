## ADDED Requirements

### Requirement: Knowledge-base management SHALL support governed single-layer CRUD
The system SHALL allow authorized users to create, list, view, update, and soft-delete single-layer knowledge bases through backend APIs and frontend management flows.

#### Scenario: Authorized user creates a knowledge base
- **WHEN** a creator or system administrator submits valid knowledge-base creation data
- **THEN** the system SHALL persist a new knowledge base and return its managed representation

#### Scenario: Authorized user updates a knowledge base
- **WHEN** a creator or system administrator submits valid updates for a managed knowledge base
- **THEN** the system SHALL persist the changes and expose the updated knowledge-base details

#### Scenario: Authorized user soft-deletes a knowledge base
- **WHEN** a creator or system administrator deletes a managed knowledge base
- **THEN** the system SHALL mark the knowledge base as deleted without requiring immediate physical removal of related records

### Requirement: Knowledge-base management SHALL enforce backend-authoritative visibility and management permissions
The system SHALL let creators and system administrators manage knowledge bases, and SHALL restrict ordinary users to viewing only knowledge bases already authorized to them.

#### Scenario: Ordinary user sees only authorized knowledge bases
- **WHEN** an ordinary user requests the knowledge-base list
- **THEN** the system SHALL return only knowledge bases the user is authorized to access

#### Scenario: Unauthorized management attempt is rejected
- **WHEN** a user without creator or system-administrator authority attempts to update or delete a knowledge base
- **THEN** the system SHALL reject the management action

### Requirement: Knowledge-base management SHALL provide paginated list and search behavior
The system SHALL provide a paginated knowledge-base list with name-based search and a default ordering that favors recently updated knowledge bases.

#### Scenario: Paginated list returns recent items first
- **WHEN** a user requests the knowledge-base list without explicit sort overrides
- **THEN** the system SHALL return active knowledge bases ordered by most recent update first

#### Scenario: Name search narrows the list
- **WHEN** a user supplies a knowledge-base name search term
- **THEN** the system SHALL filter the paginated list to knowledge bases matching that term

### Requirement: Knowledge-base management SHALL expose linked detail and document views
The system SHALL provide a knowledge-base detail view that includes core metadata and a linked list of documents belonging to that knowledge base.

#### Scenario: Detail view includes linked document status context
- **WHEN** a user opens a visible knowledge base detail view
- **THEN** the system SHALL return the knowledge-base details and document-list linkage for documents belonging to that knowledge base

### Requirement: Knowledge-base management SHALL provide a basic frontend management workflow
The frontend SHALL provide a knowledge-base list page, create and edit actions, delete confirmation, and a detail page that links to the knowledge-base document list.

#### Scenario: Frontend user completes a CRUD management loop
- **WHEN** an authorized user creates, edits, reviews, and deletes a knowledge base from the web console
- **THEN** the frontend SHALL complete those actions through the governed backend APIs and reflect the resulting state
