## Context

DevMind now includes RAG, code analysis, report generation, and general Agent execution, but the system still lacks an administrator-owned place to define which model providers and defaults are active. The platform has already established two important constraints: providers must be accessed through a unified abstraction, and sensitive values must come from environment variables. This change introduces a lightweight configuration-management layer without turning the system into a full configuration center.

## Goals / Non-Goals

**Goals:**
- Persist multiple model-provider configuration records for Qwen/OpenAI-style providers
- Let system administrators list, create, edit, enable/disable, and choose defaults for chat and embedding usage
- Expose a read-only current-effective-configuration overview
- Store only non-sensitive provider settings plus environment-variable reference names
- Enforce that only enabled records can be selected as defaults

**Non-Goals:**
- Module-specific model overrides for RAG, code analysis, or report generation
- Direct secret entry or persistence of actual API keys
- Connectivity tests, failover routing, or health probing
- Configuration version history or rollback UI
- Tenant-scoped or user-scoped model settings

## Decisions

### 1. Use a single provider-configuration record for both chat and embedding defaults
Each configuration record will contain provider identity, enabled state, base URL, API-key environment-variable reference, default chat model, default embedding model, timeout, temperature, and default-selection flags for chat/embedding. This is preferred over separate chat/embedding configuration tables because the current provider set overlaps heavily and the first iteration should optimize for operational simplicity.

### 2. Restrict the scope to global defaults managed by system administrators
The first version will manage only system-wide defaults. This avoids introducing configuration-priority rules across features and keeps runtime resolution predictable. Module-level overrides were rejected for this change because they would multiply configuration complexity before a stable default-management workflow exists.

### 3. Keep real credentials outside the database
The configuration record will store `apiKeyEnvName` and other non-sensitive parameters, but the actual secret value will continue to come from environment variables at runtime. This preserves the project’s security rule while still letting administrators understand which environment variable each provider depends on. Storing encrypted secrets in the database was rejected because it would substantially widen the security and operational scope.

### 4. Provide both overview and list views in the admin console
The frontend will expose:
- a read-only effective-configuration overview for current chat and embedding defaults
- a provider-configuration list with enabled/default status
- create/edit workflows and guarded enable/disable/default actions

This is preferred over a single global form because administrators need to understand the set of configured providers, not just the currently selected one.

### 5. Reject invalid default-state transitions instead of silently repairing them
If an administrator attempts to disable a provider that is currently the default for chat or embedding, the backend will reject the operation and require the default to be moved first. Automatic fallback was rejected for the first version because it obscures system state and makes runtime behavior less predictable.

## Risks / Trade-offs

- [More provider records means more admin UI state] → Keep the record schema small and focused on global defaults only.
- [Operators may expect immediate runtime validation of bad environment references] → Show the configured reference names clearly and defer runtime failures to execution-time configuration errors in this phase.
- [Combined chat/embedding records may become limiting later] → Keep the schema explicit enough to split in a future change if provider behavior diverges.
- [Rejecting disable-default operations may feel strict] → Favor explicit state transitions over hidden automatic fallback.

## Migration Plan

- Add model-configuration storage tables and seed-free empty defaults
- Expose admin-only APIs for overview, list, create, update, enable/disable, and default selection
- Add frontend admin pages for overview and configuration management
- Keep existing runtime behavior compatible until provider resolution is switched to read from the new defaults

## Open Questions

- None for the first implementation scope; connectivity testing and module-level overrides are intentionally deferred.
