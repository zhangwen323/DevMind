## Overview

This change adds a dedicated code-analysis workflow for pasted code snippets. The frontend gets an explicit code-analysis page, while the backend continues to route the request through the existing `AGENT` session model using a new `taskType = code-analysis`.

The goal is to create a focused developer experience without introducing repository ingestion, file uploads, or new session-type complexity. A user pastes a snippet, optionally selects a language and analysis focus, optionally asks a guiding question, and receives a structured analysis result that is preserved in session history and trace records.

## Scope Boundaries

This change includes:

- pasted code snippet analysis only
- optional language input with auto-detect fallback
- structured response sections
- reuse of existing `AGENT` conversations
- reuse of existing trace and audit mechanisms

This change excludes:

- project file uploads
- repository directory scanning
- multi-file analysis
- call-chain discovery across files
- export workflows

## Backend Design

The backend will add a dedicated code-analysis request model with:

- `codeSnippet` required
- `language` optional
- `analysisType` optional
- `question` optional

The request is handled as an Agent execution branch:

- session type remains `AGENT`
- task type becomes `code-analysis`
- optional context metadata may still be attached through existing session fields if needed later

The response should be structured into sections:

- overview
- potential issues
- optimization suggestions
- optional generated comments or explanation notes

The initial implementation may use deterministic placeholder logic or simple model-backed composition, but the API contract must already be shaped around structured output rather than a single paragraph.

## Frontend Design

The frontend gets a standalone code-analysis page rather than overloading the chat page. The page contains:

- large code input area
- optional language selector
- optional analysis-type selector
- optional guiding-question input
- submit action
- structured results view

The page may also expose the linked session identifier or session continuation behavior implicitly through the returned Agent session, but the user experience should remain centered on code-analysis rather than generic chat composition.

## Session And Trace Integration

Code-analysis requests continue to use the existing `AGENT` session model so prior analysis conversations can be reviewed and continued. Trace recording remains required. This ensures:

- code-analysis runs appear in execution traces
- users can continue follow-up analysis in session history
- operator tooling remains consistent

## Testing

Backend tests should cover:

- request validation for missing code snippets
- optional language behavior
- `code-analysis` task routing
- structured response fields
- session persistence and continuation

Frontend tests should cover:

- submitting code-analysis requests
- language and analysis-type selection
- structured result rendering
- reuse of returned Agent session state

## Tradeoff

This design intentionally favors focused snippet analysis over broader repository intelligence. That keeps the change small, aligned with the current platform maturity, and easy to extend later into file-based or repository-based code analysis if needed.
