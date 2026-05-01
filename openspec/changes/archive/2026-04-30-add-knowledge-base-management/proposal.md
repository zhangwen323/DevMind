## Why

The platform already defines document ingestion, RAG retrieval, and access control around knowledge-base scope, but it does not yet provide a governed knowledge-base management capability that users can operate end-to-end. This change is needed now so the existing upload and question-answering foundations can be used through a real management workflow instead of isolated skeleton endpoints.

## What Changes

- Add a knowledge-base management capability with backend CRUD APIs, soft deletion, pagination, name search, and detail retrieval.
- Add authorization rules for knowledge-base management so creators and system administrators can manage knowledge bases while ordinary users can only view knowledge bases already authorized to them.
- Add knowledge-base detail views that expose document-list linkage for documents belonging to a knowledge base.
- Add frontend knowledge-base list and detail flows, including create, edit, and delete actions, backed by the new APIs.
- Keep scope intentionally narrow for this change: no workspace hierarchy, no knowledge-base member assignment UI, no recycle bin, and no advanced filtering beyond name search.

## Capabilities

### New Capabilities
- `knowledge-base-management`: Manage single-layer knowledge bases through CRUD flows, scoped visibility, soft deletion, and linked document views.

### Modified Capabilities
- `document-ingestion`: Document upload and status views must operate through an explicit knowledge-base management flow and linked document listing behavior.

## Impact

- Affected backend areas: knowledge-base domain model, MyBatis mappers, services, controllers, permission checks, and document query endpoints.
- Affected frontend areas: knowledge-base routes, list/detail pages, create-edit forms, and document-list linkage.
- Affected data model: knowledge-base tables, soft-delete fields, and document-to-knowledge-base query behavior.
- Affected systems: MySQL persistence, Redis-backed access patterns if added later, and OpenSpec specs governing document and access behavior.
