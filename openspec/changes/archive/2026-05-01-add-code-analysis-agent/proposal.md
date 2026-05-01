## Why

DevMind already supports general Agent execution, session history, and traceability, but it still lacks a focused code-analysis workflow that developers can use to paste code and receive structured explanations, risk analysis, and optimization guidance. Without a dedicated code-analysis capability, this common developer task is forced into generic chat behavior and loses product clarity.

## What Changes

- Add a dedicated code-analysis page in the frontend
- Add a `code-analysis` Agent task path that accepts pasted code snippets
- Support optional language selection with automatic inference fallback
- Return structured analysis sections instead of a single free-form response
- Reuse existing `AGENT` sessions, trace recording, and audit boundaries

## Impact

- Adds a new `code-analysis-agent` capability to the main specs
- Updates `agent-execution` to support structured code-analysis task handling
- Updates `chat-session-history` so code-analysis conversations continue under the existing `AGENT` session model
- Adds backend request/response models, frontend page workflow, and verification coverage for code snippet analysis
