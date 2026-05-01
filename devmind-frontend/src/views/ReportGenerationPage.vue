<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <h1>Report Generation</h1>
        <p class="page-subtitle">
          Generate a persisted report from completed knowledge-base documents with citation traceability.
        </p>
      </div>
    </div>

    <el-card class="detail-card">
      <div class="report-grid">
        <el-form-item label="Knowledge Base">
          <el-select
            v-model="store.form.knowledgeBaseId"
            placeholder="Select a knowledge base"
            @change="store.selectKnowledgeBase"
          >
            <el-option
              v-for="item in store.knowledgeBases"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="Report Type">
          <el-select v-model="store.form.reportType">
            <el-option
              label="Daily Summary"
              value="daily-summary"
            />
            <el-option
              label="Weekly Summary"
              value="weekly-summary"
            />
            <el-option
              label="Project Summary"
              value="project-summary"
            />
            <el-option
              label="Technical Solution Draft"
              value="technical-solution-draft"
            />
          </el-select>
        </el-form-item>
      </div>

      <el-form-item label="Source Documents">
        <el-select
          v-model="store.form.documentIds"
          multiple
          collapse-tags
          collapse-tags-tooltip
          placeholder="Select completed documents"
        >
          <el-option
            v-for="document in store.availableDocuments"
            :key="document.id"
            :label="document.fileName"
            :value="document.id"
          />
        </el-select>
      </el-form-item>

      <el-form-item label="Guidance">
        <el-input
          v-model="store.form.guidance"
          type="textarea"
          :rows="4"
          placeholder="Optional instructions, focus areas, or tone guidance"
        />
      </el-form-item>

      <div class="dialog-actions">
        <el-button @click="store.resetGeneration()">
          Reset
        </el-button>
        <el-button
          type="primary"
          :loading="store.saving"
          @click="store.submitReport()"
        >
          Generate Report
        </el-button>
      </div>
    </el-card>

    <el-card
      v-if="store.generatedReport"
      class="detail-card"
    >
      <template #header>
        <div class="card-header">
          <strong>{{ store.generatedReport.report.title }}</strong>
          <span>{{ store.generatedReport.report.reportType }}</span>
        </div>
      </template>

      <div class="analysis-section">
        <h2>Generated Content</h2>
        <pre class="report-content">{{ store.generatedReport.report.content }}</pre>
      </div>

      <div class="analysis-section">
        <h2>Citations</h2>
        <ul>
          <li
            v-for="citation in store.generatedReport.report.citations"
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

import { useReportStore } from '../stores/reports'

const store = useReportStore()

onMounted(() => {
  store.loadKnowledgeBases()
})
</script>

<style scoped lang="scss">
.report-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.report-content {
  white-space: pre-wrap;
  word-break: break-word;
}

.analysis-section {
  margin-top: 20px;
}

.analysis-section h2 {
  margin-bottom: 8px;
}

@media (max-width: 900px) {
  .report-grid {
    grid-template-columns: 1fr;
  }
}
</style>
