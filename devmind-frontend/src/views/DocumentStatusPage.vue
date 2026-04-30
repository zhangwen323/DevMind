<template>
  <section class="page-section">
    <div class="page-header">
      <h1>Documents</h1>
      <el-button type="primary">
        Upload Document
      </el-button>
    </div>
    <el-table :data="rows">
      <el-table-column
        prop="fileName"
        label="File"
      />
      <el-table-column
        prop="status"
        label="Status"
      />
      <el-table-column
        prop="updatedAt"
        label="Updated"
      />
    </el-table>
  </section>
</template>

<script setup>
const rows = [
  { fileName: 'guide.md', status: 'UPLOADED', updatedAt: '2026-04-30' },
  { fileName: 'sql-spec.pdf', status: 'COMPLETED', updatedAt: '2026-04-30' }
]
</script>
