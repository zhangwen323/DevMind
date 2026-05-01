import { defineStore } from 'pinia'

import {
  createKnowledgeBase,
  deleteKnowledgeBase,
  fetchKnowledgeBaseDetail,
  fetchKnowledgeBases,
  updateKnowledgeBase
} from '../api/knowledgeBases'

function emptyForm() {
  return {
    id: null,
    name: '',
    description: ''
  }
}

export const useKnowledgeBaseStore = defineStore('knowledgeBases', {
  state: () => ({
    items: [],
    total: 0,
    page: 1,
    size: 10,
    keyword: '',
    detail: null,
    loading: false,
    saving: false,
    dialogVisible: false,
    form: emptyForm()
  }),
  actions: {
    async loadKnowledgeBases() {
      this.loading = true
      try {
        const result = await fetchKnowledgeBases({
          keyword: this.keyword,
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
    async loadKnowledgeBaseDetail(knowledgeBaseId) {
      this.loading = true
      try {
        this.detail = await fetchKnowledgeBaseDetail(knowledgeBaseId)
      } finally {
        this.loading = false
      }
    },
    openCreateDialog() {
      this.form = emptyForm()
      this.dialogVisible = true
    },
    openEditDialog(knowledgeBase) {
      this.form = {
        id: knowledgeBase.id,
        name: knowledgeBase.name,
        description: knowledgeBase.description || ''
      }
      this.dialogVisible = true
    },
    closeDialog() {
      this.dialogVisible = false
      this.form = emptyForm()
    },
    async createKnowledgeBase(payload) {
      this.saving = true
      try {
        await createKnowledgeBase(payload)
        await this.loadKnowledgeBases()
        this.closeDialog()
      } finally {
        this.saving = false
      }
    },
    async updateKnowledgeBase(knowledgeBaseId, payload) {
      this.saving = true
      try {
        await updateKnowledgeBase(knowledgeBaseId, payload)
        await this.loadKnowledgeBases()
        if (this.detail?.id === knowledgeBaseId) {
          await this.loadKnowledgeBaseDetail(knowledgeBaseId)
        }
        this.closeDialog()
      } finally {
        this.saving = false
      }
    },
    async saveKnowledgeBase() {
      if (this.form.id) {
        await this.updateKnowledgeBase(this.form.id, {
          name: this.form.name,
          description: this.form.description
        })
        return
      }

      await this.createKnowledgeBase({
        name: this.form.name,
        description: this.form.description
      })
    },
    async deleteKnowledgeBase(knowledgeBaseId) {
      this.saving = true
      try {
        await deleteKnowledgeBase(knowledgeBaseId)
        await this.loadKnowledgeBases()
        if (this.detail?.id === knowledgeBaseId) {
          this.detail = null
        }
      } finally {
        this.saving = false
      }
    }
  }
})
