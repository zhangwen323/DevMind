import { beforeEach, describe, expect, test, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

vi.mock('../api/knowledgeBases', () => ({
  createKnowledgeBase: vi.fn(),
  deleteKnowledgeBase: vi.fn(),
  fetchKnowledgeBaseDetail: vi.fn(),
  fetchKnowledgeBases: vi.fn(),
  updateKnowledgeBase: vi.fn()
}))

import {
  createKnowledgeBase,
  deleteKnowledgeBase,
  fetchKnowledgeBaseDetail,
  fetchKnowledgeBases,
  updateKnowledgeBase
} from '../api/knowledgeBases'
import { useKnowledgeBaseStore } from '../stores/knowledgeBases'

describe('knowledge base store workflows', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  test('loads paginated list with keyword search', async () => {
    fetchKnowledgeBases.mockResolvedValue({
      items: [{ id: 10, name: 'Payments' }],
      total: 1,
      page: 1,
      size: 10
    })

    const store = useKnowledgeBaseStore()
    store.keyword = 'pay'

    await store.loadKnowledgeBases()

    expect(fetchKnowledgeBases).toHaveBeenCalledWith({ keyword: 'pay', page: 1, size: 10 })
    expect(store.items).toEqual([{ id: 10, name: 'Payments' }])
    expect(store.total).toBe(1)
  })

  test('creates a knowledge base and refreshes the list', async () => {
    createKnowledgeBase.mockResolvedValue({ id: 12, name: 'Architecture' })
    fetchKnowledgeBases.mockResolvedValue({
      items: [{ id: 12, name: 'Architecture' }],
      total: 1,
      page: 1,
      size: 10
    })

    const store = useKnowledgeBaseStore()

    await store.createKnowledgeBase({
      name: 'Architecture',
      description: 'Architecture guides'
    })

    expect(createKnowledgeBase).toHaveBeenCalledWith({
      name: 'Architecture',
      description: 'Architecture guides'
    })
    expect(store.items[0].name).toBe('Architecture')
  })

  test('updates and deletes a knowledge base through managed workflows', async () => {
    updateKnowledgeBase.mockResolvedValue({ id: 10, name: 'Payments Core' })
    deleteKnowledgeBase.mockResolvedValue()
    fetchKnowledgeBases.mockResolvedValue({
      items: [{ id: 11, name: 'Orders' }],
      total: 1,
      page: 1,
      size: 10
    })

    const store = useKnowledgeBaseStore()

    await store.updateKnowledgeBase(10, {
      name: 'Payments Core',
      description: 'Updated payment docs'
    })
    await store.deleteKnowledgeBase(10)

    expect(updateKnowledgeBase).toHaveBeenCalledWith(10, {
      name: 'Payments Core',
      description: 'Updated payment docs'
    })
    expect(deleteKnowledgeBase).toHaveBeenCalledWith(10)
    expect(store.items).toEqual([{ id: 11, name: 'Orders' }])
  })

  test('loads knowledge base detail with linked documents', async () => {
    fetchKnowledgeBaseDetail.mockResolvedValue({
      id: 10,
      name: 'Payments',
      documents: [{ id: 1001, fileName: 'payments.md', parseStatus: 'COMPLETED' }]
    })

    const store = useKnowledgeBaseStore()

    await store.loadKnowledgeBaseDetail(10)

    expect(fetchKnowledgeBaseDetail).toHaveBeenCalledWith(10)
    expect(store.detail.name).toBe('Payments')
    expect(store.detail.documents[0].fileName).toBe('payments.md')
  })
})
