<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <h1>Reports</h1>
        <p class="page-subtitle">
          Review persisted reports generated from governed knowledge-base documents.
        </p>
      </div>
    </div>

    <el-card class="detail-card">
      <div class="toolbar">
        <el-select
          v-model="store.reportTypeFilter"
          clearable
          placeholder="Filter by report type"
          @change="store.loadReports()"
        >
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
      </div>

      <el-table
        :data="store.items"
        :loading="store.loading"
      >
        <el-table-column
          prop="title"
          label="Title"
        />
        <el-table-column
          prop="reportType"
          label="Type"
          width="220"
        />
        <el-table-column
          prop="knowledgeBaseName"
          label="Knowledge Base"
        />
        <el-table-column
          prop="createdByUsername"
          label="Created By"
          width="160"
        />
        <el-table-column
          label="Actions"
          width="140"
        >
          <template #default="{ row }">
            <el-button
              type="primary"
              link
              @click="$router.push(`/reports/${row.id}`)"
            >
              View
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>
  </section>
</template>

<script setup>
import { onMounted } from 'vue'

import { useReportStore } from '../stores/reports'

const store = useReportStore()

onMounted(() => {
  store.loadReports()
})
</script>
