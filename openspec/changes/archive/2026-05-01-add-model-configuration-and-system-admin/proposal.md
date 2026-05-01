## Why

DevMind now has multiple AI-facing capabilities, but it still lacks an administrator-managed source of truth for which model provider and defaults the system should use. A governed model-configuration surface is needed now so the platform can manage Qwen/OpenAI defaults consistently without hardcoding provider behavior into feature modules.

## What Changes

- Add a system-administrator capability to manage model-provider configuration records through backend APIs and frontend admin workflows.
- Support provider configuration listing, creation, editing, enable/disable control, and default selection for chat and embedding usage.
- Add a read-only current-configuration overview that shows which provider and model settings are currently effective.
- Persist only non-sensitive configuration and environment-variable reference names; continue to load real API keys from environment variables at runtime.
- Enforce that default chat and embedding selections can only point to enabled provider records.

## Capabilities

### New Capabilities
- `model-configuration-and-system-admin`: Manage global default model-provider records, current effective configuration overview, and administrator-only model configuration workflows.

### Modified Capabilities
- `user-access-control`: Extend protected administrative access rules and audit expectations to cover model-configuration management operations.
- `platform-foundation`: Define the platform-level rule that sensitive model credentials remain environment-backed while non-sensitive provider defaults are system-managed.

## Impact

- Backend: new model-configuration persistence, service logic, administrative APIs, and runtime configuration resolution
- Frontend: new administrator configuration overview and provider configuration management views
- Security: administrator-only access enforcement and audit records for protected configuration changes
- Runtime behavior: shared provider defaults become data-driven rather than hardcoded
