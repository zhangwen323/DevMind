import { defineStore } from 'pinia'

import { analyzeCodeSnippet } from '../api/codeAnalysis'

function emptyForm() {
  return {
    sessionId: null,
    language: null,
    analysisType: 'GENERAL',
    question: '',
    codeSnippet: ''
  }
}

export const useCodeAnalysisStore = defineStore('codeAnalysis', {
  state: () => ({
    form: emptyForm(),
    result: null,
    loading: false
  }),
  actions: {
    async submit() {
      this.loading = true
      try {
        const result = await analyzeCodeSnippet({
          sessionId: this.form.sessionId,
          language: this.form.language,
          analysisType: this.form.analysisType,
          question: this.form.question,
          codeSnippet: this.form.codeSnippet
        })
        this.result = result
        this.form.sessionId = result.sessionId
      } finally {
        this.loading = false
      }
    },
    reset() {
      this.form = emptyForm()
      this.result = null
    }
  }
})
