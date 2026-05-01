<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <el-button
          link
          type="primary"
          @click="router.push('/knowledge-bases')"
        >
          Back to Knowledge Bases
        </el-button>
        <h1>{{ knowledgeBaseStore.detail?.name || 'Knowledge Base Detail' }}</h1>
        <p class="page-subtitle">
          Review metadata and linked document processing status.
        </p>
      </div>
    </div>

    <el-card
      v-loading="knowledgeBaseStore.loading"
      class="detail-card"
    >
      <template v-if="knowledgeBaseStore.detail">
        <div class="detail-grid">
          <div>
            <span class="detail-label">Owner</span>
            <strong>{{ knowledgeBaseStore.detail.ownerUsername }}</strong>
          </div>
          <div>
            <span class="detail-label">Visibility</span>
            <strong>{{ knowledgeBaseStore.detail.visibility }}</strong>
          </div>
        </div>
        <p class="detail-description">
          {{ knowledgeBaseStore.detail.description || 'No description provided.' }}
        </p>
      </template>
    </el-card>

    <el-card class="detail-card">
      <template #header>
        <div class="card-header">
          <span>Documents</span>
          <el-button
            link
            type="primary"
            @click="router.push('/documents')"
          >
            Open Document Queue
          </el-button>
        </div>
      </template>

      <el-table
        :data="knowledgeBaseStore.detail?.documents || []"
        empty-text="No documents linked to this knowledge base"
      >
        <el-table-column
          prop="fileName"
          label="File"
          min-width="220"
        />
        <el-table-column
          prop="fileType"
          label="Type"
          width="120"
        />
        <el-table-column
          prop="parseStatus"
          label="Status"
          width="160"
        />
      </el-table>
    </el-card>
  </section>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

import { useKnowledgeBaseStore } from '../stores/knowledgeBases'

const route = useRoute()
const router = useRouter()
const knowledgeBaseStore = useKnowledgeBaseStore()

onMounted(() => {
  knowledgeBaseStore.loadKnowledgeBaseDetail(Number(route.params.knowledgeBaseId))
})
</script>
