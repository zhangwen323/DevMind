import { defineStore } from 'pinia'

import {
  createModelConfiguration,
  fetchModelConfigurationOverview,
  fetchModelConfigurations,
  setDefaultChatConfiguration,
  setDefaultEmbeddingConfiguration,
  updateModelConfiguration,
  updateModelConfigurationEnabled
} from '../api/modelConfigurations'

function emptyForm() {
  return {
    id: null,
    providerCode: '',
    displayName: '',
    baseUrl: '',
    apiKeyEnvName: '',
    chatModel: '',
    embeddingModel: '',
    timeoutMs: 30000,
    temperature: 0.2,
    notes: '',
    enabled: true
  }
}

export const useModelConfigurationStore = defineStore('modelConfigurations', {
  state: () => ({
    overview: null,
    items: [],
    loading: false,
    saving: false,
    dialogVisible: false,
    form: emptyForm()
  }),
  actions: {
    async loadOverview() {
      this.overview = await fetchModelConfigurationOverview()
    },
    async loadConfigurations() {
      this.loading = true
      try {
        this.items = await fetchModelConfigurations()
      } finally {
        this.loading = false
      }
    },
    async refresh() {
      await Promise.all([
        this.loadOverview(),
        this.loadConfigurations()
      ])
    },
    openCreateDialog() {
      this.form = emptyForm()
      this.dialogVisible = true
    },
    openEditDialog(item) {
      this.form = {
        id: item.id,
        providerCode: item.providerCode,
        displayName: item.displayName,
        baseUrl: item.baseUrl || '',
        apiKeyEnvName: item.apiKeyEnvName,
        chatModel: item.chatModel,
        embeddingModel: item.embeddingModel,
        timeoutMs: item.timeoutMs,
        temperature: item.temperature,
        notes: item.notes || '',
        enabled: item.enabled
      }
      this.dialogVisible = true
    },
    closeDialog() {
      this.dialogVisible = false
      this.form = emptyForm()
    },
    async save() {
      this.saving = true
      try {
        if (this.form.id) {
          await updateModelConfiguration(this.form.id, {
            providerCode: this.form.providerCode,
            displayName: this.form.displayName,
            baseUrl: this.form.baseUrl || null,
            apiKeyEnvName: this.form.apiKeyEnvName,
            chatModel: this.form.chatModel,
            embeddingModel: this.form.embeddingModel,
            timeoutMs: this.form.timeoutMs,
            temperature: this.form.temperature,
            notes: this.form.notes || null,
            enabled: this.form.enabled
          })
        } else {
          await createModelConfiguration({
            providerCode: this.form.providerCode,
            displayName: this.form.displayName,
            baseUrl: this.form.baseUrl || null,
            apiKeyEnvName: this.form.apiKeyEnvName,
            chatModel: this.form.chatModel,
            embeddingModel: this.form.embeddingModel,
            timeoutMs: this.form.timeoutMs,
            temperature: this.form.temperature,
            notes: this.form.notes || null,
            enabled: this.form.enabled
          })
        }
        await this.refresh()
        this.closeDialog()
      } finally {
        this.saving = false
      }
    },
    async setEnabled(configId, enabled) {
      this.saving = true
      try {
        await updateModelConfigurationEnabled(configId, enabled)
        await this.refresh()
      } finally {
        this.saving = false
      }
    },
    async setDefaultChat(configId) {
      this.saving = true
      try {
        await setDefaultChatConfiguration(configId)
        await this.refresh()
      } finally {
        this.saving = false
      }
    },
    async setDefaultEmbedding(configId) {
      this.saving = true
      try {
        await setDefaultEmbeddingConfiguration(configId)
        await this.refresh()
      } finally {
        this.saving = false
      }
    }
  }
})
