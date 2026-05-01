## 1. Backend Trace Query Foundations

- [x] 1.1 Define backend trace query models for administrator list rows, administrator full details, and user-visible redacted summaries
- [x] 1.2 Implement trace repository queries for recent-first pagination, `status` filtering, `agentName` filtering, keyword search, and session-linked trace lookup
- [x] 1.3 Enforce permission boundaries so only system administrators can access global trace queries and full details
- [x] 1.4 Enforce owner-only access for session-linked user trace summaries

## 2. Backend Trace APIs And Session Integration

- [x] 2.1 Add administrator APIs for global trace list and full trace detail
- [x] 2.2 Add session-linked user APIs for redacted trace summary access
- [x] 2.3 Implement trace redaction and summary projection rules for user-visible responses
- [x] 2.4 Update chat session integration so linked traces can be discovered from session context without exposing global trace access

## 3. Frontend Trace Operations View

- [x] 3.1 Add frontend API client methods and state management for administrator trace queries and user trace summaries
- [x] 3.2 Build the administrator trace list page with keyword search, `status` filter, `agentName` filter, and recent-first ordering
- [x] 3.3 Build the administrator trace detail view with steps, tool calls, summaries, timing, and error information
- [x] 3.4 Add session-linked trace summary entry points and user-visible summary view for owned chat sessions

## 4. Verification And OpenSpec Consistency

- [x] 4.1 Add or update backend tests for admin authorization, user authorization, filtering, keyword search, session linkage, and redaction
- [x] 4.2 Add or update frontend tests for administrator trace browsing, detail loading, and user session-linked trace summaries
- [x] 4.3 Run backend and frontend verification commands and fix regressions
- [x] 4.4 Update OpenSpec artifacts if implementation reveals necessary scope or behavior adjustments before completion
