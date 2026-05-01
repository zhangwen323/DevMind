## 1. Backend Domain And Persistence

- [x] 1.1 Add knowledge-base persistence schema and Flyway migration with soft-delete support
- [x] 1.2 Implement knowledge-base domain models, MyBatis mappers, and repository queries for create, update, detail, pagination, and name search
- [x] 1.3 Implement backend authorization checks so creators and system administrators can manage knowledge bases while ordinary users only see authorized knowledge bases

## 2. Backend APIs And Document Linkage

- [x] 2.1 Add knowledge-base CRUD APIs with unified response contracts and validation rules
- [x] 2.2 Add a knowledge-base-scoped document listing API for detail views
- [x] 2.3 Ensure soft-deleted knowledge bases are excluded from normal list, detail, update, delete, and document-list flows

## 3. Frontend Management Workflow

- [x] 3.1 Add knowledge-base routes, API client methods, and Pinia state needed for management flows
- [x] 3.2 Build the knowledge-base list page with pagination, name search, and create entry point
- [x] 3.3 Build create and edit form flows plus delete confirmation for managed knowledge bases
- [x] 3.4 Build the knowledge-base detail page with linked document listing and status visibility

## 4. Verification And OpenSpec Consistency

- [x] 4.1 Add or update backend tests for CRUD, authorization, pagination, name search, soft delete, and document-linkage behavior
- [x] 4.2 Add or update frontend tests for list, create or edit, delete, and detail workflows
- [x] 4.3 Run backend and frontend verification commands and fix any regressions
- [x] 4.4 Update OpenSpec artifacts if implementation reveals necessary scope or behavior adjustments before completion
