## 1. Backend Domain And Persistence

- [x] 1.1 Add report-generation storage schema for report records and document-scope linkage
- [x] 1.2 Implement report domain models, mappers, and persistence services
- [x] 1.3 Add report list and report detail APIs with knowledge-base-aware access control

## 2. Agent Report Generation Flow

- [x] 2.1 Add the `report-generation` task path to Agent execution with structured output and citations
- [x] 2.2 Enforce request validation for visible knowledge base, eligible `COMPLETED` documents, and supported report types
- [x] 2.3 Persist successful report-generation output as a durable report record linked to session and trace context

## 3. Frontend Report Workflows

- [x] 3.1 Add report generation API and state management for knowledge-base selection, document selection, and report submission
- [x] 3.2 Build a report generation page with report-type selection and optional guidance input
- [x] 3.3 Build report list and report detail pages that display persisted content and citations

## 4. Verification And Alignment

- [x] 4.1 Add backend tests for request validation, successful generation, and persisted report retrieval
- [x] 4.2 Add frontend tests for report submission and report viewing workflows
- [x] 4.3 Run backend and frontend verification for the completed report-generation flow
