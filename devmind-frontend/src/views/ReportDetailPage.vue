<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <h1>Report Detail</h1>
        <p class="page-subtitle">
          Inspect the generated report, selected source documents, and source citations.
        </p>
      </div>
    </div>

    <el-card
      v-if="store.detail"
      class="detail-card"
    >
      <template #header>
        <div class="card-header">
          <strong>{{ store.detail.title }}</strong>
          <span>{{ store.detail.reportType }}</span>
        </div>
      </template>

      <div class="analysis-section">
        <h2>Knowledge Base</h2>
        <p>{{ store.detail.knowledgeBaseName }}</p>
      </div>

      <div class="analysis-section">
        <h2>Content</h2>
        <pre class="report-content">{{ store.detail.content }}</pre>
      </div>

      <div class="analysis-section">
        <h2>Source Documents</h2>
        <ul>
          <li
            v-for="document in store.detail.documents"
            :key="document.id"
          >
            {{ document.fileName }} ({{ document.parseStatus }})
          </li>
        </ul>
      </div>

      <div class="analysis-section">
        <h2>Citations</h2>
        <ul>
          <li
            v-for="citation in store.detail.citations"
            :key="`${citation.documentId}-${citation.documentName}`"
          >
            {{ citation.documentName }}: {{ citation.excerpt }}
          </li>
        </ul>
      </div>
    </el-card>
  </section>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute } from 'vue-router'

import { useReportStore } from '../stores/reports'

const route = useRoute()
const store = useReportStore()

onMounted(() => {
  store.loadReportDetail(Number(route.params.reportId))
})
</script>

<style scoped lang="scss">
.analysis-section {
  margin-top: 20px;
}

.analysis-section h2 {
  margin-bottom: 8px;
}

.report-content {
  white-space: pre-wrap;
  word-break: break-word;
}
</style>
