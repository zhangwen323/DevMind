import { defineStore } from 'pinia'

import {
  fetchAdminTraceDetail,
  fetchAdminTraces,
  fetchSessionTraceDetail,
  fetchSessionTraces
} from '../api/agentTraces'

export const useAgentTraceStore = defineStore('agentTraces', {
  state: () => ({
    items: [],
    total: 0,
    page: 1,
    size: 10,
    filters: {
      keyword: '',
      status: '',
      agentName: ''
    },
    activeAdminTrace: null,
    sessionTraces: [],
    activeSessionTrace: null,
    loading: false
  }),
  actions: {
    async loadAdminTraces() {
      this.loading = true
      try {
        const result = await fetchAdminTraces({
          keyword: this.filters.keyword,
          status: this.filters.status,
          agentName: this.filters.agentName,
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
    async openAdminTrace(traceKey) {
      this.loading = true
      try {
        this.activeAdminTrace = await fetchAdminTraceDetail(traceKey)
      } finally {
        this.loading = false
      }
    },
    async loadSessionTraces(sessionId) {
      this.loading = true
      try {
        this.sessionTraces = await fetchSessionTraces(sessionId)
        this.activeSessionTrace = null
      } finally {
        this.loading = false
      }
    },
    async openSessionTrace(sessionId, traceKey) {
      this.loading = true
      try {
        this.activeSessionTrace = await fetchSessionTraceDetail(sessionId, traceKey)
      } finally {
        this.loading = false
      }
    },
    clearSessionTraces() {
      this.sessionTraces = []
      this.activeSessionTrace = null
    }
  }
})
