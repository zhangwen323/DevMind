<template>
  <section class="page-section">
    <div class="page-header">
      <h1>Agent Traces</h1>
    </div>
    <el-table :data="rows">
      <el-table-column
        prop="agentName"
        label="Agent"
      />
      <el-table-column
        prop="stepName"
        label="Step"
      />
      <el-table-column
        prop="status"
        label="Status"
      />
    </el-table>
  </section>
</template>

<script setup>
const rows = [
  { agentName: 'rag-agent', stepName: 'route', status: 'SUCCESS' }
]
</script>
