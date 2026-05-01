<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <h1>Model Configuration</h1>
        <p class="page-subtitle">
          Manage global provider defaults, environment-key references, and enabled provider state.
        </p>
      </div>
      <el-button
        type="primary"
        @click="store.openCreateDialog()"
      >
        Add Provider
      </el-button>
    </div>

    <div
      v-if="store.overview"
      class="overview-grid"
    >
      <el-card class="detail-card">
        <template #header>
          <strong>Current Chat Default</strong>
        </template>
        <div v-if="store.overview.chat">
          <p>{{ store.overview.chat.displayName }} ({{ store.overview.chat.providerCode }})</p>
          <p>Model: {{ store.overview.chat.model }}</p>
          <p>Base URL: {{ store.overview.chat.baseUrl || 'N/A' }}</p>
          <p>API Key Env: {{ store.overview.chat.apiKeyEnvName }}</p>
        </div>
        <p v-else>No default chat provider configured.</p>
      </el-card>

      <el-card class="detail-card">
        <template #header>
          <strong>Current Embedding Default</strong>
        </template>
        <div v-if="store.overview.embedding">
          <p>{{ store.overview.embedding.displayName }} ({{ store.overview.embedding.providerCode }})</p>
          <p>Model: {{ store.overview.embedding.model }}</p>
          <p>Base URL: {{ store.overview.embedding.baseUrl || 'N/A' }}</p>
          <p>API Key Env: {{ store.overview.embedding.apiKeyEnvName }}</p>
        </div>
        <p v-else>No default embedding provider configured.</p>
      </el-card>

      <el-card class="detail-card">
        <template #header>
          <strong>Enabled Providers</strong>
        </template>
        <p class="metric-number">
          {{ store.overview.enabledProviderCount }}
        </p>
      </el-card>
    </div>

    <el-card class="detail-card">
      <el-table
        v-loading="store.loading"
        :data="store.items"
      >
        <el-table-column
          prop="displayName"
          label="Provider"
          min-width="180"
        />
        <el-table-column
          prop="chatModel"
          label="Chat Model"
          min-width="160"
        />
        <el-table-column
          prop="embeddingModel"
          label="Embedding Model"
          min-width="180"
        />
        <el-table-column
          prop="apiKeyEnvName"
          label="API Key Env"
          min-width="180"
        />
        <el-table-column
          label="State"
          width="160"
        >
          <template #default="{ row }">
            <div class="state-column">
              <el-tag :type="row.enabled ? 'success' : 'info'">
                {{ row.enabled ? 'Enabled' : 'Disabled' }}
              </el-tag>
              <el-tag
                v-if="row.defaultChat"
                type="warning"
              >
                Chat Default
              </el-tag>
              <el-tag
                v-if="row.defaultEmbedding"
                type="primary"
              >
                Embedding Default
              </el-tag>
            </div>
          </template>
        </el-table-column>
        <el-table-column
          label="Actions"
          width="320"
        >
          <template #default="{ row }">
            <div class="row-actions">
              <el-button
                link
                @click="store.openEditDialog(row)"
              >
                Edit
              </el-button>
              <el-button
                link
                @click="toggleEnabled(row)"
              >
                {{ row.enabled ? 'Disable' : 'Enable' }}
              </el-button>
              <el-button
                link
                type="primary"
                :disabled="!row.enabled"
                @click="store.setDefaultChat(row.id)"
              >
                Set Chat Default
              </el-button>
              <el-button
                link
                type="primary"
                :disabled="!row.enabled"
                @click="store.setDefaultEmbedding(row.id)"
              >
                Set Embedding Default
              </el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <el-dialog
      v-model="store.dialogVisible"
      :title="store.form.id ? 'Edit Provider Configuration' : 'Add Provider Configuration'"
      width="640px"
      @closed="store.closeDialog()"
    >
      <el-form
        label-position="top"
        @submit.prevent="store.save()"
      >
        <div class="form-grid">
          <el-form-item label="Provider Code">
            <el-input v-model="store.form.providerCode" />
          </el-form-item>
          <el-form-item label="Display Name">
            <el-input v-model="store.form.displayName" />
          </el-form-item>
          <el-form-item label="Chat Model">
            <el-input v-model="store.form.chatModel" />
          </el-form-item>
          <el-form-item label="Embedding Model">
            <el-input v-model="store.form.embeddingModel" />
          </el-form-item>
          <el-form-item label="Base URL">
            <el-input v-model="store.form.baseUrl" />
          </el-form-item>
          <el-form-item label="API Key Env Name">
            <el-input v-model="store.form.apiKeyEnvName" />
          </el-form-item>
          <el-form-item label="Timeout (ms)">
            <el-input-number
              v-model="store.form.timeoutMs"
              :min="1000"
              :step="1000"
            />
          </el-form-item>
          <el-form-item label="Temperature">
            <el-input-number
              v-model="store.form.temperature"
              :min="0"
              :max="2"
              :step="0.1"
            />
          </el-form-item>
        </div>
        <el-form-item label="Notes">
          <el-input
            v-model="store.form.notes"
            type="textarea"
            :rows="3"
          />
        </el-form-item>
        <el-form-item label="Enabled">
          <el-switch v-model="store.form.enabled" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="dialog-actions">
          <el-button @click="store.closeDialog()">
            Cancel
          </el-button>
          <el-button
            type="primary"
            :loading="store.saving"
            @click="store.save()"
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

import { useModelConfigurationStore } from '../stores/modelConfigurations'

const store = useModelConfigurationStore()

onMounted(() => {
  store.refresh()
})

function toggleEnabled(row) {
  store.setEnabled(row.id, !row.enabled)
}
</script>

<style scoped lang="scss">
.overview-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(3, minmax(0, 1fr));
  margin-bottom: 16px;
}

.metric-number {
  font-size: 32px;
  font-weight: 700;
}

.state-column {
  display: flex;
  flex-direction: column;
  gap: 6px;
}

.form-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

@media (max-width: 960px) {
  .overview-grid,
  .form-grid {
    grid-template-columns: 1fr;
  }
}
</style>
