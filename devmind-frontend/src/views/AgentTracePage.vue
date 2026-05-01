<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <h1>Agent Traces</h1>
        <p class="page-subtitle">
          Search recent agent executions and inspect read-only operator details.
        </p>
      </div>
    </div>

    <div class="toolbar-card trace-filters">
      <el-input
        v-model="traceStore.filters.keyword"
        placeholder="Search trace key, session title, or error"
        clearable
        @keyup.enter="refresh"
        @clear="refresh"
      />
      <el-select
        v-model="traceStore.filters.status"
        placeholder="Status"
        clearable
        @change="refresh"
      >
        <el-option
          label="SUCCESS"
          value="SUCCESS"
        />
        <el-option
          label="FAILED"
          value="FAILED"
        />
      </el-select>
      <el-select
        v-model="traceStore.filters.agentName"
        placeholder="Agent"
        clearable
        @change="refresh"
      >
        <el-option
          label="router-agent"
          value="router-agent"
        />
        <el-option
          label="rag-agent"
          value="rag-agent"
        />
        <el-option
          label="sql-agent"
          value="sql-agent"
        />
        <el-option
          label="report-agent"
          value="report-agent"
        />
      </el-select>
    </div>

    <div class="trace-grid">
      <el-card class="detail-card">
        <el-table
          :data="traceStore.items"
          @row-click="openTrace"
        >
          <el-table-column
            prop="traceKey"
            label="Trace"
            min-width="220"
          />
          <el-table-column
            prop="agentName"
            label="Agent"
            min-width="140"
          />
          <el-table-column
            prop="status"
            label="Status"
            width="110"
          />
          <el-table-column
            prop="stepCount"
            label="Steps"
            width="90"
          />
          <el-table-column
            prop="totalLatencyMs"
            label="Latency"
            width="120"
          >
            <template #default="{ row }">
              {{ row.totalLatencyMs }} ms
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <el-card
        v-if="traceStore.activeAdminTrace"
        class="detail-card"
      >
        <template #header>
          <div class="trace-detail-header">
            <div>
              <strong>{{ traceStore.activeAdminTrace.traceKey }}</strong>
              <p class="page-subtitle">
                {{ traceStore.activeAdminTrace.agentName }} | {{ traceStore.activeAdminTrace.status }}
              </p>
            </div>
            <span>{{ traceStore.activeAdminTrace.totalLatencyMs }} ms</span>
          </div>
        </template>

        <div class="trace-step-list">
          <article
            v-for="step in traceStore.activeAdminTrace.steps"
            :key="`${traceStore.activeAdminTrace.traceKey}-${step.stepName}-${step.createdAt}`"
            class="trace-step-card"
          >
            <header class="trace-step-header">
              <strong>{{ step.stepName }}</strong>
              <span>{{ step.status }} | {{ step.latencyMs }} ms</span>
            </header>
            <p v-if="step.toolName">
              Tool: {{ step.toolName }}
            </p>
            <p v-if="step.errorMessage">
              Error: {{ step.errorMessage }}
            </p>
            <pre class="trace-payload">{{ step.inputPayload }}</pre>
            <pre class="trace-payload">{{ step.outputPayload }}</pre>
          </article>
        </div>
      </el-card>
    </div>
  </section>
</template>

<script setup>
import { onMounted } from 'vue'

import { useAgentTraceStore } from '../stores/agentTraces'

const traceStore = useAgentTraceStore()

onMounted(() => {
  traceStore.loadAdminTraces()
})

function refresh() {
  traceStore.loadAdminTraces()
}

function openTrace(row) {
  traceStore.openAdminTrace(row.traceKey)
}
</script>

<style scoped lang="scss">
.trace-filters {
  display: grid;
  gap: 12px;
  grid-template-columns: 2fr 1fr 1fr;
  margin-bottom: 16px;
}

.trace-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: 1.2fr 1fr;
}

.trace-detail-header,
.trace-step-header {
  align-items: center;
  display: flex;
  gap: 12px;
  justify-content: space-between;
}

.trace-step-list {
  display: grid;
  gap: 12px;
}

.trace-step-card {
  background: #f8fafc;
  border: 1px solid #dbe4f0;
  border-radius: 12px;
  padding: 12px;
}

.trace-payload {
  background: #111827;
  border-radius: 8px;
  color: #f9fafb;
  font-size: 12px;
  overflow-x: auto;
  padding: 10px;
  white-space: pre-wrap;
}

@media (max-width: 960px) {
  .trace-filters,
  .trace-grid {
    grid-template-columns: 1fr;
  }
}
</style>
