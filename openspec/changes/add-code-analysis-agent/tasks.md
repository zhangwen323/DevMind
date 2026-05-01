## 1. Backend Code Analysis Models

- [ ] 1.1 Define request and structured response models for pasted code snippet analysis
- [ ] 1.2 Add validation rules for required code content and optional language / analysis metadata
- [ ] 1.3 Define the `code-analysis` Agent task branch while keeping session type as `AGENT`

## 2. Backend Agent And Session Integration

- [ ] 2.1 Implement backend code-analysis execution flow that produces structured output sections
- [ ] 2.2 Reuse existing Agent session persistence so code-analysis requests create or append to `AGENT` sessions
- [ ] 2.3 Ensure code-analysis requests continue to emit trace and audit records through existing Agent execution boundaries

## 3. Frontend Code Analysis Workflow

- [ ] 3.1 Add frontend API client methods and state management for code-analysis requests and structured responses
- [ ] 3.2 Build a dedicated code-analysis page with code input, optional language selector, optional analysis-type selector, and optional guiding question
- [ ] 3.3 Render structured analysis results with sections for overview, potential issues, optimization suggestions, and generated comments or explanation notes
- [ ] 3.4 Preserve returned Agent session linkage so follow-up analysis can continue within existing session history behavior

## 4. Verification And OpenSpec Consistency

- [ ] 4.1 Add or update backend tests for validation, task routing, structured output, and session persistence
- [ ] 4.2 Add or update frontend tests for request submission, selector behavior, structured rendering, and session reuse
- [ ] 4.3 Run backend and frontend verification commands and fix regressions
- [ ] 4.4 Update OpenSpec artifacts if implementation reveals necessary scope or behavior adjustments before completion
