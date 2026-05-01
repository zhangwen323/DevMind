import { defineStore } from 'pinia'

import { fetchKnowledgeBases } from '../api/knowledgeBases'
import {
  askRag,
  deleteChatSession,
  executeAgent,
  fetchChatSessionDetail,
  fetchChatSessions
} from '../api/chatSessions'

function emptyComposer() {
  return {
    sessionId: null,
    sessionType: 'RAG',
    knowledgeBaseId: null,
    contextType: '',
    contextId: '',
    message: ''
  }
}

function inferAgentTask(contextType) {
  if (contextType === 'REPORT') {
    return { taskType: 'report', toolScope: 'report:write' }
  }
  return { taskType: 'agent', toolScope: 'agent:write' }
}

export const useChatSessionStore = defineStore('chatSessions', {
  state: () => ({
    items: [],
    total: 0,
    page: 1,
    size: 10,
    keyword: '',
    sessionType: 'ALL',
    activeSession: null,
    loading: false,
    saving: false,
    knowledgeBaseOptions: [],
    composer: emptyComposer()
  }),
  actions: {
    async loadSessions() {
      this.loading = true
      try {
        const result = await fetchChatSessions({
          keyword: this.keyword,
          sessionType: this.sessionType,
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
    async loadKnowledgeBaseOptions() {
      const result = await fetchKnowledgeBases({ page: 1, size: 100 })
      this.knowledgeBaseOptions = result.items || []
      if (!this.composer.knowledgeBaseId && this.knowledgeBaseOptions[0]) {
        this.composer.knowledgeBaseId = this.knowledgeBaseOptions[0].id
      }
    },
    async openSession(sessionId) {
      this.loading = true
      try {
        this.activeSession = await fetchChatSessionDetail(sessionId)
        this.composer = {
          sessionId: this.activeSession.id,
          sessionType: this.activeSession.sessionType,
          knowledgeBaseId: this.activeSession.knowledgeBaseId ?? null,
          contextType: this.activeSession.contextType || '',
          contextId: this.activeSession.contextId || '',
          message: ''
        }
      } finally {
        this.loading = false
      }
    },
    startNewSession(sessionType = 'RAG') {
      this.activeSession = null
      this.composer = {
        ...emptyComposer(),
        sessionType,
        knowledgeBaseId: sessionType === 'RAG' ? this.knowledgeBaseOptions[0]?.id ?? null : null
      }
    },
    async sendMessage() {
      const message = this.composer.message.trim()
      if (!message) {
        return
      }

      this.saving = true
      try {
        if (this.composer.sessionType === 'RAG') {
          const result = await askRag({
            sessionId: this.composer.sessionId,
            knowledgeBaseId: this.composer.knowledgeBaseId,
            question: message
          })
          await this.openSession(result.sessionId)
        } else {
          const agentMeta = inferAgentTask(this.composer.contextType)
          const result = await executeAgent({
            sessionId: this.composer.sessionId,
            knowledgeBaseId: this.composer.knowledgeBaseId,
            contextType: this.composer.contextType || null,
            contextId: this.composer.contextId || null,
            taskType: agentMeta.taskType,
            userInput: message,
            toolScope: agentMeta.toolScope
          })
          await this.openSession(result.sessionId)
        }

        this.composer.message = ''
        await this.loadSessions()
      } finally {
        this.saving = false
      }
    },
    async deleteSession(sessionId) {
      this.saving = true
      try {
        await deleteChatSession(sessionId)
        if (this.activeSession?.id === sessionId) {
          this.activeSession = null
          this.startNewSession(this.sessionType === 'AGENT' ? 'AGENT' : 'RAG')
        }
        await this.loadSessions()
      } finally {
        this.saving = false
      }
    }
  }
})
