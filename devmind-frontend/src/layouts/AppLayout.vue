<template>
  <div class="layout-shell">
    <aside class="sidebar">
      <div class="brand">
        DevMind
      </div>
      <el-menu
        :default-active="$route.path"
        router
      >
        <el-menu-item index="/knowledge-bases">
          Knowledge Bases
        </el-menu-item>
        <el-menu-item index="/documents">
          Documents
        </el-menu-item>
        <el-menu-item index="/chat">
          Chat
        </el-menu-item>
        <el-menu-item index="/code-analysis">
          Code Analysis
        </el-menu-item>
        <el-menu-item index="/reports/generate">
          Generate Report
        </el-menu-item>
        <el-menu-item index="/reports">
          Reports
        </el-menu-item>
        <el-menu-item index="/agent-traces">
          Agent Traces
        </el-menu-item>
      </el-menu>
    </aside>
    <main class="content">
      <header class="topbar">
        <span>{{ authStore.username || 'Guest' }}</span>
        <el-button
          type="primary"
          plain
          @click="logout"
        >
          Logout
        </el-button>
      </header>
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'

import { useAuthStore } from '../stores/auth'

const authStore = useAuthStore()
const router = useRouter()

function logout() {
  authStore.logout()
  router.push('/login')
}
</script>
