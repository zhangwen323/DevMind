<template>
  <div class="login-page">
    <el-card class="login-card">
      <template #header>
        Sign in to DevMind
      </template>
      <el-form @submit.prevent="submit">
        <el-form-item label="Username">
          <el-input v-model="form.username" />
        </el-form-item>
        <el-form-item label="Password">
          <el-input
            v-model="form.password"
            show-password
            type="password"
          />
        </el-form-item>
        <el-button
          type="primary"
          @click="submit"
        >
          Login
        </el-button>
      </el-form>
    </el-card>
  </div>
</template>

<script setup>
import { reactive } from 'vue'
import { useRouter } from 'vue-router'

import http from '../api/http'
import { useAuthStore } from '../stores/auth'

const form = reactive({
  username: 'admin',
  password: 'secret'
})

const authStore = useAuthStore()
const router = useRouter()

async function submit() {
  const { data } = await http.post('/api/auth/login', form)
  authStore.login(data.data.token, data.data.username)
  router.push('/knowledge-bases')
}
</script>
