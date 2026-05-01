import { beforeEach, describe, expect, test, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

vi.mock('../api/chatSessions', () => ({
  askRag: vi.fn(),
  deleteChatSession: vi.fn(),
  executeAgent: vi.fn(),
  fetchChatSessionDetail: vi.fn(),
  fetchChatSessions: vi.fn()
}))

import {
  askRag,
  deleteChatSession,
  executeAgent,
  fetchChatSessionDetail,
  fetchChatSessions
} from '../api/chatSessions'
import { useChatSessionStore } from '../stores/chatSessions'

describe('chat session store workflows', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  test('loads session list with title search and type filter', async () => {
    fetchChatSessions.mockResolvedValue({
      items: [{ id: 100, title: 'Payments follow-up', sessionType: 'RAG' }],
      total: 1,
      page: 1,
      size: 10
    })

    const store = useChatSessionStore()
    store.keyword = 'pay'
    store.sessionType = 'RAG'

    await store.loadSessions()

    expect(fetchChatSessions).toHaveBeenCalledWith({
      keyword: 'pay',
      sessionType: 'RAG',
      page: 1,
      size: 10
    })
    expect(store.items[0].title).toBe('Payments follow-up')
  })

  test('loads session detail and message history', async () => {
    fetchChatSessionDetail.mockResolvedValue({
      id: 100,
      sessionType: 'RAG',
      knowledgeBaseId: 10,
      messages: [
        { id: 1, roleCode: 'USER', content: 'How does payment retry work?' },
        { id: 2, roleCode: 'ASSISTANT', content: 'Grounded answer.' }
      ]
    })

    const store = useChatSessionStore()

    await store.openSession(100)

    expect(fetchChatSessionDetail).toHaveBeenCalledWith(100)
    expect(store.activeSession.id).toBe(100)
    expect(store.activeSession.messages).toHaveLength(2)
  })

  test('continues a rag session and refreshes detail', async () => {
    askRag.mockResolvedValue({
      sessionId: 100,
      knowledgeBaseId: 10,
      answer: 'Grounded answer for follow-up',
      references: []
    })
    fetchChatSessionDetail.mockResolvedValue({
      id: 100,
      sessionType: 'RAG',
      knowledgeBaseId: 10,
      messages: [
        { id: 1, roleCode: 'USER', content: 'Original question' },
        { id: 2, roleCode: 'ASSISTANT', content: 'Original answer' },
        { id: 3, roleCode: 'USER', content: 'Follow-up question' },
        { id: 4, roleCode: 'ASSISTANT', content: 'Grounded answer for follow-up' }
      ]
    })

    const store = useChatSessionStore()
    store.composer = {
      sessionType: 'RAG',
      knowledgeBaseId: 10,
      contextType: '',
      contextId: '',
      message: 'Follow-up question',
      sessionId: 100
    }

    await store.sendMessage()

    expect(askRag).toHaveBeenCalledWith({
      sessionId: 100,
      knowledgeBaseId: 10,
      question: 'Follow-up question'
    })
    expect(store.activeSession.messages).toHaveLength(4)
    expect(store.composer.message).toBe('')
  })

  test('continues an agent session and supports deletion', async () => {
    executeAgent.mockResolvedValue({
      sessionId: 101,
      agentName: 'report-agent',
      responseText: 'Agent handled: summarize weekly delivery work',
      allowed: true,
      trace: { stepName: 'route', status: 'SUCCESS', latencyMs: 5 }
    })
    fetchChatSessionDetail.mockResolvedValue({
      id: 101,
      sessionType: 'AGENT',
      contextType: 'REPORT',
      contextId: 'weekly-1',
      messages: [
        { id: 1, roleCode: 'USER', content: 'summarize weekly delivery work' },
        { id: 2, roleCode: 'ASSISTANT', content: 'Agent handled: summarize weekly delivery work' }
      ]
    })
    deleteChatSession.mockResolvedValue()
    fetchChatSessions.mockResolvedValue({
      items: [],
      total: 0,
      page: 1,
      size: 10
    })

    const store = useChatSessionStore()
    store.composer = {
      sessionType: 'AGENT',
      knowledgeBaseId: null,
      contextType: 'REPORT',
      contextId: 'weekly-1',
      message: 'summarize weekly delivery work',
      sessionId: 101
    }

    await store.sendMessage()
    await store.deleteSession(101)

    expect(executeAgent).toHaveBeenCalledWith({
      sessionId: 101,
      knowledgeBaseId: null,
      contextType: 'REPORT',
      contextId: 'weekly-1',
      taskType: 'report',
      userInput: 'summarize weekly delivery work',
      toolScope: 'report:write'
    })
    expect(deleteChatSession).toHaveBeenCalledWith(101)
    expect(store.items).toEqual([])
    expect(store.activeSession).toBe(null)
  })
})
