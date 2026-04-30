<template>
  <section class="page-section">
    <div class="page-header">
      <h1>Knowledge Bases</h1>
      <el-button type="primary">
        Create Knowledge Base
      </el-button>
    </div>
    <el-table :data="rows">
      <el-table-column
        prop="name"
        label="Name"
      />
      <el-table-column
        prop="visibility"
        label="Visibility"
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
  { name: 'Authentication', visibility: 'PRIVATE', updatedAt: '2026-04-30' },
  { name: 'Orders', visibility: 'PRIVATE', updatedAt: '2026-04-30' }
]
</script>
