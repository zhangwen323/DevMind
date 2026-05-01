import { defineStore } from 'pinia'

import { fetchKnowledgeBaseDetail, fetchKnowledgeBases } from '../api/knowledgeBases'
import { createReport, fetchReportDetail, fetchReports } from '../api/reports'

function emptyForm() {
  return {
    sessionId: null,
    knowledgeBaseId: null,
    documentIds: [],
    reportType: 'daily-summary',
    guidance: ''
  }
}

export const useReportStore = defineStore('reports', {
  state: () => ({
    form: emptyForm(),
    knowledgeBases: [],
    availableDocuments: [],
    items: [],
    total: 0,
    page: 1,
    size: 10,
    reportTypeFilter: '',
    detail: null,
    loading: false,
    saving: false,
    generatedReport: null
  }),
  actions: {
    async loadKnowledgeBases() {
      const result = await fetchKnowledgeBases({
        page: 1,
        size: 100
      })
      this.knowledgeBases = result.items || []
    },
    async selectKnowledgeBase(knowledgeBaseId) {
      this.form.knowledgeBaseId = knowledgeBaseId
      this.form.documentIds = []
      if (!knowledgeBaseId) {
        this.availableDocuments = []
        return
      }
      const detail = await fetchKnowledgeBaseDetail(knowledgeBaseId)
      this.availableDocuments = (detail.documents || []).filter((document) => document.parseStatus === 'COMPLETED')
    },
    async submitReport() {
      this.saving = true
      try {
        const result = await createReport({
          sessionId: this.form.sessionId,
          knowledgeBaseId: this.form.knowledgeBaseId,
          documentIds: this.form.documentIds,
          reportType: this.form.reportType,
          guidance: this.form.guidance
        })
        this.generatedReport = result
        this.form.sessionId = result.sessionId
      } finally {
        this.saving = false
      }
    },
    async loadReports() {
      this.loading = true
      try {
        const result = await fetchReports({
          reportType: this.reportTypeFilter || null,
          page: this.page,
          size: this.size
        })
        this.items = result.items || []
        this.total = result.total || 0
        this.page = result.page || this.page
        this.size = result.size || this.size
      } finally {
        this.loading = false
      }
    },
    async loadReportDetail(reportId) {
      this.loading = true
      try {
        this.detail = await fetchReportDetail(reportId)
      } finally {
        this.loading = false
      }
    },
    resetGeneration() {
      this.form = emptyForm()
      this.availableDocuments = []
      this.generatedReport = null
    }
  }
})
