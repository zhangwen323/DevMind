## 1. Backend Model Configuration Domain

- [x] 1.1 Add storage schema for model-provider configuration records and default-selection fields
- [x] 1.2 Implement provider-configuration models, mappers, and services
- [x] 1.3 Add admin-only APIs for overview, list, create, update, enable/disable, and default selection

## 2. Runtime Rules And Access Control

- [x] 2.1 Enforce administrator-only access to model-configuration management operations
- [x] 2.2 Enforce enabled-state rules for default chat and embedding selection
- [x] 2.3 Resolve runtime provider secrets through environment-variable reference names instead of persisted secret values

## 3. Frontend Admin Workflows

- [x] 3.1 Add model-configuration API and state management
- [x] 3.2 Build an admin overview panel for current effective chat and embedding defaults
- [x] 3.3 Build configuration list and create/edit/default-state workflows for provider records

## 4. Verification And Alignment

- [x] 4.1 Add backend tests for protected access, default-state rules, and configuration persistence
- [x] 4.2 Add frontend tests for overview, listing, and administrative state transitions
- [x] 4.3 Run backend and frontend verification for the completed model-configuration flow
