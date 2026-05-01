<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <h1>Knowledge Bases</h1>
        <p class="page-subtitle">
          Manage single-layer knowledge bases and jump into document status details.
        </p>
      </div>
      <el-button
        type="primary"
        @click="knowledgeBaseStore.openCreateDialog()"
      >
        Create Knowledge Base
      </el-button>
    </div>

    <div class="toolbar-card">
      <el-input
        v-model="knowledgeBaseStore.keyword"
        class="search-input"
        placeholder="Search knowledge bases by name"
        clearable
        @keyup.enter="refreshList"
        @clear="refreshList"
      />
      <el-button @click="refreshList">
        Search
      </el-button>
    </div>

    <el-table
      v-loading="knowledgeBaseStore.loading"
      :data="knowledgeBaseStore.items"
      empty-text="No knowledge bases found"
    >
      <el-table-column
        prop="name"
        label="Name"
        min-width="220"
      />
      <el-table-column
        prop="visibility"
        label="Visibility"
        width="120"
      />
      <el-table-column
        prop="ownerUsername"
        label="Owner"
        width="140"
      />
      <el-table-column
        prop="updatedAt"
        label="Updated"
        min-width="180"
      />
      <el-table-column
        label="Actions"
        width="260"
      >
        <template #default="{ row }">
          <div class="row-actions">
            <el-button
              link
              type="primary"
              @click="goToDetail(row.id)"
            >
              View
            </el-button>
            <el-button
              link
              @click="knowledgeBaseStore.openEditDialog(row)"
            >
              Edit
            </el-button>
            <el-button
              link
              type="danger"
              @click="confirmDelete(row)"
            >
              Delete
            </el-button>
          </div>
        </template>
      </el-table-column>
    </el-table>

    <div class="pagination-row">
      <el-pagination
        v-model:current-page="knowledgeBaseStore.page"
        v-model:page-size="knowledgeBaseStore.size"
        background
        layout="total, prev, pager, next"
        :total="knowledgeBaseStore.total"
        @current-change="refreshList"
      />
    </div>

    <el-dialog
      v-model="knowledgeBaseStore.dialogVisible"
      :title="knowledgeBaseStore.form.id ? 'Edit Knowledge Base' : 'Create Knowledge Base'"
      width="560px"
      @closed="knowledgeBaseStore.closeDialog()"
    >
      <el-form
        label-position="top"
        @submit.prevent="submitForm"
      >
        <el-form-item label="Name">
          <el-input
            v-model="knowledgeBaseStore.form.name"
            maxlength="255"
          />
        </el-form-item>
        <el-form-item label="Description">
          <el-input
            v-model="knowledgeBaseStore.form.description"
            type="textarea"
            :rows="4"
          />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-actions">
          <el-button @click="knowledgeBaseStore.closeDialog()">
            Cancel
          </el-button>
          <el-button
            type="primary"
            :loading="knowledgeBaseStore.saving"
            @click="submitForm"
          >
            Save
          </el-button>
        </div>
      </template>
    </el-dialog>
  </section>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRouter } from 'vue-router'

import { useKnowledgeBaseStore } from '../stores/knowledgeBases'

const knowledgeBaseStore = useKnowledgeBaseStore()
const router = useRouter()

onMounted(() => {
  refreshList()
})

function refreshList() {
  knowledgeBaseStore.loadKnowledgeBases()
}

function submitForm() {
  knowledgeBaseStore.saveKnowledgeBase()
}

function goToDetail(knowledgeBaseId) {
  router.push(`/knowledge-bases/${knowledgeBaseId}`)
}

function confirmDelete(knowledgeBase) {
  if (!window.confirm(`Delete knowledge base "${knowledgeBase.name}"?`)) {
    return
  }
  knowledgeBaseStore.deleteKnowledgeBase(knowledgeBase.id)
}
</script>
