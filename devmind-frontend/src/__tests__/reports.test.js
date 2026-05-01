import { beforeEach, describe, expect, test, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

vi.mock('../api/knowledgeBases', () => ({
  fetchKnowledgeBaseDetail: vi.fn(),
  fetchKnowledgeBases: vi.fn()
}))

vi.mock('../api/reports', () => ({
  createReport: vi.fn(),
  fetchReportDetail: vi.fn(),
  fetchReports: vi.fn()
}))

import { fetchKnowledgeBaseDetail, fetchKnowledgeBases } from '../api/knowledgeBases'
import { createReport, fetchReportDetail, fetchReports } from '../api/reports'
import { useReportStore } from '../stores/reports'

describe('report generation workflows', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  test('loads knowledge bases and eligible completed documents', async () => {
    fetchKnowledgeBases.mockResolvedValue({
      items: [{ id: 10, name: 'Payments' }],
      total: 1,
      page: 1,
      size: 100
    })
    fetchKnowledgeBaseDetail.mockResolvedValue({
      id: 10,
      name: 'Payments',
      documents: [
        { id: 1001, fileName: 'payments.md', parseStatus: 'COMPLETED' },
        { id: 1002, fileName: 'draft.md', parseStatus: 'PARSING' }
      ]
    })

    const store = useReportStore()

    await store.loadKnowledgeBases()
    await store.selectKnowledgeBase(10)

    expect(fetchKnowledgeBases).toHaveBeenCalledWith({ page: 1, size: 100 })
    expect(fetchKnowledgeBaseDetail).toHaveBeenCalledWith(10)
    expect(store.availableDocuments).toEqual([
      { id: 1001, fileName: 'payments.md', parseStatus: 'COMPLETED' }
    ])
  })

  test('submits a report request and preserves session context', async () => {
    createReport.mockResolvedValue({
      reportId: 301,
      sessionId: 501,
      agentName: 'report-agent',
      report: {
        id: 301,
        title: 'Weekly Summary - Payments',
        reportType: 'weekly-summary',
        citations: [{ documentId: 1001, documentName: 'payments.md', excerpt: 'Derived from payments.md.' }]
      }
    })

    const store = useReportStore()
    store.form.knowledgeBaseId = 10
    store.form.documentIds = [1001]
    store.form.reportType = 'weekly-summary'
    store.form.guidance = 'Focus on delivery milestones'

    await store.submitReport()

    expect(createReport).toHaveBeenCalledWith({
      sessionId: null,
      knowledgeBaseId: 10,
      documentIds: [1001],
      reportType: 'weekly-summary',
      guidance: 'Focus on delivery milestones'
    })
    expect(store.generatedReport.reportId).toBe(301)
    expect(store.form.sessionId).toBe(501)
  })

  test('loads persisted report list and detail views', async () => {
    fetchReports.mockResolvedValue({
      items: [{ id: 301, title: 'Weekly Summary - Payments' }],
      total: 1,
      page: 1,
      size: 10
    })
    fetchReportDetail.mockResolvedValue({
      id: 301,
      title: 'Weekly Summary - Payments',
      documents: [{ id: 1001, fileName: 'payments.md' }],
      citations: [{ documentId: 1001, documentName: 'payments.md', excerpt: 'Derived from payments.md.' }]
    })

    const store = useReportStore()

    await store.loadReports()
    await store.loadReportDetail(301)

    expect(fetchReports).toHaveBeenCalledWith({
      reportType: null,
      page: 1,
      size: 10
    })
    expect(fetchReportDetail).toHaveBeenCalledWith(301)
    expect(store.detail.title).toBe('Weekly Summary - Payments')
  })
})
