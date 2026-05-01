import { beforeEach, describe, expect, test, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

vi.mock('../api/codeAnalysis', () => ({
  analyzeCodeSnippet: vi.fn()
}))

import { analyzeCodeSnippet } from '../api/codeAnalysis'
import { useCodeAnalysisStore } from '../stores/codeAnalysis'

describe('code analysis store workflows', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  test('submits a code snippet and stores structured results', async () => {
    analyzeCodeSnippet.mockResolvedValue({
      sessionId: 201,
      agentName: 'code-analysis-agent',
      analysis: {
        language: 'java',
        overview: 'This method dereferences user directly.',
        potentialIssues: ['Possible NullPointerException when user is null.'],
        optimizationSuggestions: ['Guard against null before reading user name.'],
        generatedNotes: ['Add a null check comment near the method entry.']
      }
    })

    const store = useCodeAnalysisStore()
    store.form.language = 'java'
    store.form.analysisType = 'ISSUES'
    store.form.question = 'Focus on null-safety'
    store.form.codeSnippet = 'public String demo(User user) { return user.getName(); }'

    await store.submit()

    expect(analyzeCodeSnippet).toHaveBeenCalledWith({
      sessionId: null,
      language: 'java',
      analysisType: 'ISSUES',
      question: 'Focus on null-safety',
      codeSnippet: 'public String demo(User user) { return user.getName(); }'
    })
    expect(store.result.analysis.language).toBe('java')
    expect(store.result.analysis.potentialIssues).toHaveLength(1)
    expect(store.form.sessionId).toBe(201)
  })

  test('allows language omission and preserves returned session for follow-up', async () => {
    analyzeCodeSnippet.mockResolvedValue({
      sessionId: 202,
      agentName: 'code-analysis-agent',
      analysis: {
        language: 'sql',
        overview: 'This query selects all columns.',
        potentialIssues: ['Broad selection may fetch unnecessary data.'],
        optimizationSuggestions: ['Select only required columns.'],
        generatedNotes: ['Document expected result shape for maintainers.']
      }
    })

    const store = useCodeAnalysisStore()
    store.form.analysisType = 'EXPLAIN'
    store.form.codeSnippet = "SELECT * FROM orders WHERE status = 'FAILED'"

    await store.submit()

    expect(analyzeCodeSnippet).toHaveBeenCalledWith({
      sessionId: null,
      language: null,
      analysisType: 'EXPLAIN',
      question: '',
      codeSnippet: "SELECT * FROM orders WHERE status = 'FAILED'"
    })
    expect(store.result.analysis.language).toBe('sql')
    expect(store.form.sessionId).toBe(202)
  })
})
