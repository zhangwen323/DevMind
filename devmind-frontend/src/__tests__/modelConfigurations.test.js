import { beforeEach, describe, expect, test, vi } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'

vi.mock('../api/modelConfigurations', () => ({
  createModelConfiguration: vi.fn(),
  fetchModelConfigurationOverview: vi.fn(),
  fetchModelConfigurations: vi.fn(),
  setDefaultChatConfiguration: vi.fn(),
  setDefaultEmbeddingConfiguration: vi.fn(),
  updateModelConfiguration: vi.fn(),
  updateModelConfigurationEnabled: vi.fn()
}))

import {
  createModelConfiguration,
  fetchModelConfigurationOverview,
  fetchModelConfigurations,
  setDefaultChatConfiguration,
  setDefaultEmbeddingConfiguration,
  updateModelConfiguration,
  updateModelConfigurationEnabled
} from '../api/modelConfigurations'
import { useModelConfigurationStore } from '../stores/modelConfigurations'

describe('model configuration admin workflows', () => {
  beforeEach(() => {
    setActivePinia(createPinia())
    vi.clearAllMocks()
  })

  test('loads overview and configuration list', async () => {
    fetchModelConfigurationOverview.mockResolvedValue({
      chat: { providerCode: 'qwen', model: 'qwen-plus', apiKeyEnvName: 'QWEN_API_KEY' },
      embedding: { providerCode: 'qwen', model: 'text-embedding-v4', apiKeyEnvName: 'QWEN_API_KEY' },
      enabledProviderCount: 1
    })
    fetchModelConfigurations.mockResolvedValue([
      { id: 1, providerCode: 'qwen', displayName: 'Qwen', enabled: true, defaultChat: true, defaultEmbedding: true }
    ])

    const store = useModelConfigurationStore()
    await store.refresh()

    expect(fetchModelConfigurationOverview).toHaveBeenCalled()
    expect(fetchModelConfigurations).toHaveBeenCalled()
    expect(store.overview.enabledProviderCount).toBe(1)
    expect(store.items[0].providerCode).toBe('qwen')
  })

  test('creates and updates provider configuration records', async () => {
    createModelConfiguration.mockResolvedValue({ id: 1 })
    updateModelConfiguration.mockResolvedValue({ id: 1 })
    fetchModelConfigurationOverview.mockResolvedValue({ chat: null, embedding: null, enabledProviderCount: 0 })
    fetchModelConfigurations.mockResolvedValue([])

    const store = useModelConfigurationStore()
    store.form.providerCode = 'qwen'
    store.form.displayName = 'Qwen'
    store.form.apiKeyEnvName = 'QWEN_API_KEY'
    store.form.chatModel = 'qwen-plus'
    store.form.embeddingModel = 'text-embedding-v4'

    await store.save()

    expect(createModelConfiguration).toHaveBeenCalledWith({
      providerCode: 'qwen',
      displayName: 'Qwen',
      baseUrl: null,
      apiKeyEnvName: 'QWEN_API_KEY',
      chatModel: 'qwen-plus',
      embeddingModel: 'text-embedding-v4',
      timeoutMs: 30000,
      temperature: 0.2,
      notes: null,
      enabled: true
    })

    store.openEditDialog({
      id: 1,
      providerCode: 'qwen',
      displayName: 'Qwen',
      baseUrl: null,
      apiKeyEnvName: 'QWEN_API_KEY',
      chatModel: 'qwen-plus',
      embeddingModel: 'text-embedding-v4',
      timeoutMs: 30000,
      temperature: 0.2,
      notes: null,
      enabled: true
    })
    store.form.displayName = 'Qwen Updated'

    await store.save()

    expect(updateModelConfiguration).toHaveBeenCalledWith(1, expect.objectContaining({
      displayName: 'Qwen Updated'
    }))
  })

  test('toggles enabled state and default assignments', async () => {
    updateModelConfigurationEnabled.mockResolvedValue({})
    setDefaultChatConfiguration.mockResolvedValue({})
    setDefaultEmbeddingConfiguration.mockResolvedValue({})
    fetchModelConfigurationOverview.mockResolvedValue({ chat: null, embedding: null, enabledProviderCount: 0 })
    fetchModelConfigurations.mockResolvedValue([])

    const store = useModelConfigurationStore()

    await store.setEnabled(5, false)
    await store.setDefaultChat(5)
    await store.setDefaultEmbedding(5)

    expect(updateModelConfigurationEnabled).toHaveBeenCalledWith(5, false)
    expect(setDefaultChatConfiguration).toHaveBeenCalledWith(5)
    expect(setDefaultEmbeddingConfiguration).toHaveBeenCalledWith(5)
  })
})
