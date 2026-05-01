<template>
  <section class="page-section">
    <div class="page-header">
      <div>
        <h1>Code Analysis</h1>
        <p class="page-subtitle">
          Paste a code snippet to get structured analysis, risks, and improvement suggestions.
        </p>
      </div>
    </div>

    <el-card class="detail-card">
      <div class="code-analysis-grid">
        <el-form-item label="Language">
          <el-select
            v-model="store.form.language"
            clearable
            placeholder="Auto-detect"
          >
            <el-option
              label="Java"
              value="java"
            />
            <el-option
              label="Python"
              value="python"
            />
            <el-option
              label="JavaScript"
              value="javascript"
            />
            <el-option
              label="SQL"
              value="sql"
            />
          </el-select>
        </el-form-item>

        <el-form-item label="Analysis Type">
          <el-select v-model="store.form.analysisType">
            <el-option
              label="General"
              value="GENERAL"
            />
            <el-option
              label="Explain"
              value="EXPLAIN"
            />
            <el-option
              label="Issues"
              value="ISSUES"
            />
            <el-option
              label="Comments"
              value="COMMENTS"
            />
          </el-select>
        </el-form-item>
      </div>

      <el-form-item label="Guiding Question">
        <el-input
          v-model="store.form.question"
          placeholder="Optional focus, such as null-safety or transaction boundaries"
        />
      </el-form-item>

      <el-form-item label="Code Snippet">
        <el-input
          v-model="store.form.codeSnippet"
          type="textarea"
          :rows="16"
          placeholder="Paste your code here..."
        />
      </el-form-item>

      <div class="dialog-actions">
        <el-button @click="store.reset()">
          Reset
        </el-button>
        <el-button
          type="primary"
          :loading="store.loading"
          @click="store.submit()"
        >
          Analyze
        </el-button>
      </div>
    </el-card>

    <el-card
      v-if="store.result"
      class="detail-card"
    >
      <template #header>
        <div class="card-header">
          <strong>{{ store.result.agentName }}</strong>
          <span>Language: {{ store.result.analysis.language }}</span>
        </div>
      </template>

      <div class="analysis-section">
        <h2>Overview</h2>
        <p>{{ store.result.analysis.overview }}</p>
      </div>

      <div class="analysis-section">
        <h2>Potential Issues</h2>
        <ul>
          <li
            v-for="item in store.result.analysis.potentialIssues"
            :key="item"
          >
            {{ item }}
          </li>
        </ul>
      </div>

      <div class="analysis-section">
        <h2>Optimization Suggestions</h2>
        <ul>
          <li
            v-for="item in store.result.analysis.optimizationSuggestions"
            :key="item"
          >
            {{ item }}
          </li>
        </ul>
      </div>

      <div class="analysis-section">
        <h2>Generated Notes</h2>
        <ul>
          <li
            v-for="item in store.result.analysis.generatedNotes"
            :key="item"
          >
            {{ item }}
          </li>
        </ul>
      </div>
    </el-card>
  </section>
</template>

<script setup>
import { useCodeAnalysisStore } from '../stores/codeAnalysis'

const store = useCodeAnalysisStore()
</script>

<style scoped lang="scss">
.code-analysis-grid {
  display: grid;
  gap: 16px;
  grid-template-columns: repeat(2, minmax(0, 1fr));
}

.analysis-section {
  margin-top: 20px;
}

.analysis-section h2 {
  margin-bottom: 8px;
}

@media (max-width: 900px) {
  .code-analysis-grid {
    grid-template-columns: 1fr;
  }
}
</style>
