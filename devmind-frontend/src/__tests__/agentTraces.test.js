import { beforeEach, describe, expect, test, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

vi.mock('../api/agentTraces', () => ({
  fetchAdminTraceDetail: vi.fn(),
  fetchAdminTraces: vi.fn(),
  fetchSessionTraceDetail: vi.fn(),
  fetchSessionTraces: vi.fn()
}))

import {
  fetchAdminTraceDetail,
  fetchAdminTraces,
  fetchSessionTraceDetail,
  fetchSessionTraces
} from '../api/agentTraces'
import { useAgentTraceStore } from '../stores/agentTraces'

describe('agent trace store workflows', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  test('loads administrator trace list with filters', async () => {
    fetchAdminTraces.mockResolvedValue({
      items: [{ traceKey: 'trace-weekly-1', agentName: 'report-agent', status: 'SUCCESS' }],
      total: 1,
      page: 1,
      size: 10
    })

    const store = useAgentTraceStore()
    store.filters.keyword = 'weekly'
    store.filters.status = 'SUCCESS'
    store.filters.agentName = 'report-agent'

    await store.loadAdminTraces()

    expect(fetchAdminTraces).toHaveBeenCalledWith({
      keyword: 'weekly',
      status: 'SUCCESS',
      agentName: 'report-agent',
      page: 1,
      size: 10
    })
    expect(store.items[0].traceKey).toBe('trace-weekly-1')
  })

  test('loads administrator trace detail', async () => {
    fetchAdminTraceDetail.mockResolvedValue({
      traceKey: 'trace-weekly-1',
      agentName: 'report-agent',
      steps: [
        { stepName: 'route', status: 'SUCCESS' },
        { stepName: 'tool-execute', toolName: 'report-writer', status: 'SUCCESS' }
      ]
    })

    const store = useAgentTraceStore()

    await store.openAdminTrace('trace-weekly-1')

    expect(fetchAdminTraceDetail).toHaveBeenCalledWith('trace-weekly-1')
    expect(store.activeAdminTrace.steps).toHaveLength(2)
  })

  test('loads session-linked trace summaries for a chat session', async () => {
    fetchSessionTraces.mockResolvedValue([
      {
        traceKey: 'trace-weekly-1',
        agentName: 'report-agent',
        status: 'SUCCESS',
        steps: [{ stepName: 'route', summary: 'Step route completed successfully.' }]
      }
    ])

    const store = useAgentTraceStore()

    await store.loadSessionTraces(101)

    expect(fetchSessionTraces).toHaveBeenCalledWith(101)
    expect(store.sessionTraces[0].traceKey).toBe('trace-weekly-1')
  })

  test('loads redacted detail for a session-linked trace', async () => {
    fetchSessionTraceDetail.mockResolvedValue({
      traceKey: 'trace-weekly-1',
      agentName: 'report-agent',
      steps: [
        { stepName: 'route', summary: 'Step route completed successfully.' },
        { stepName: 'tool-execute', summary: 'Tool report-writer completed successfully.' }
      ]
    })

    const store = useAgentTraceStore()

    await store.openSessionTrace(101, 'trace-weekly-1')

    expect(fetchSessionTraceDetail).toHaveBeenCalledWith(101, 'trace-weekly-1')
    expect(store.activeSessionTrace.steps[1].summary).toBe('Tool report-writer completed successfully.')
  })
})
