<template>
  <section class="chat-page">
    <aside class="chat-sidebar">
      <div class="page-header chat-sidebar-header">
        <div>
          <h1>Conversations</h1>
          <p class="page-subtitle">
            Search and continue RAG or Agent sessions.
          </p>
        </div>
        <el-button
          type="primary"
          @click="newSession"
        >
          New
        </el-button>
      </div>

      <div class="toolbar-card chat-sidebar-filters">
        <el-input
          v-model="chatStore.keyword"
          placeholder="Search session title"
          clearable
          @keyup.enter="refreshSessions"
          @clear="refreshSessions"
        />
        <el-select
          v-model="chatStore.sessionType"
          placeholder="Type"
          @change="refreshSessions"
        >
          <el-option
            label="All"
            value="ALL"
          />
          <el-option
            label="RAG"
            value="RAG"
          />
          <el-option
            label="Agent"
            value="AGENT"
          />
        </el-select>
      </div>

      <div class="chat-session-list">
        <button
          v-for="session in chatStore.items"
          :key="session.id"
          class="chat-session-card"
          :class="{ 'chat-session-card--active': chatStore.activeSession?.id === session.id }"
          @click="chatStore.openSession(session.id)"
        >
          <div class="chat-session-meta">
            <strong>{{ session.title }}</strong>
            <span>{{ session.sessionType }}</span>
          </div>
          <div class="chat-session-actions">
            <span>{{ formatUpdated(session.updatedAt) }}</span>
            <el-button
              link
              type="danger"
              @click.stop="deleteSession(session)"
            >
              Delete
            </el-button>
          </div>
        </button>
      </div>
    </aside>

    <div class="chat-main">
      <div class="page-header">
        <div>
          <h1>{{ chatStore.activeSession?.title || 'New Conversation' }}</h1>
          <p class="page-subtitle">
            {{ chatStore.activeSession ? 'Continue the selected session.' : 'Start a new RAG or Agent conversation.' }}
          </p>
        </div>
      </div>

      <el-card class="detail-card">
        <div class="composer-grid">
          <el-form-item label="Session Type">
            <el-select
              v-model="chatStore.composer.sessionType"
              :disabled="Boolean(chatStore.activeSession)"
            >
              <el-option
                label="RAG"
                value="RAG"
              />
              <el-option
                label="Agent"
                value="AGENT"
              />
            </el-select>
          </el-form-item>

          <el-form-item
            v-if="chatStore.composer.sessionType === 'RAG'"
            label="Knowledge Base"
          >
            <el-select
              v-model="chatStore.composer.knowledgeBaseId"
              filterable
            >
              <el-option
                v-for="kb in chatStore.knowledgeBaseOptions"
                :key="kb.id"
                :label="kb.name"
                :value="kb.id"
              />
            </el-select>
          </el-form-item>

          <template v-else>
            <el-form-item label="Context Type">
              <el-input
                v-model="chatStore.composer.contextType"
                placeholder="REPORT / TASK / REVIEW"
              />
            </el-form-item>
            <el-form-item label="Context Id">
              <el-input
                v-model="chatStore.composer.contextId"
                placeholder="weekly-1"
              />
            </el-form-item>
          </template>
        </div>

        <div class="chat-message-list">
          <div
            v-for="message in chatStore.activeSession?.messages || []"
            :key="message.id"
            class="chat-message"
            :class="{
              'chat-message--user': message.roleCode === 'USER',
              'chat-message--assistant': message.roleCode === 'ASSISTANT'
            }"
          >
            <div class="chat-message-role">
              {{ message.roleCode }}
            </div>
            <div class="chat-message-content">
              {{ message.content }}
            </div>
          </div>
        </div>

        <div
          v-if="chatStore.activeSession?.sessionType === 'AGENT' && traceStore.sessionTraces.length"
          class="trace-summary-section"
        >
          <div class="section-header">
            <h2>Execution Summaries</h2>
            <p class="page-subtitle">
              Review user-visible Agent execution summaries linked to this conversation.
            </p>
          </div>

          <button
            v-for="trace in traceStore.sessionTraces"
            :key="trace.traceKey"
            class="trace-summary-card"
            @click="traceStore.openSessionTrace(chatStore.activeSession.id, trace.traceKey)"
          >
            <div class="trace-summary-meta">
              <strong>{{ trace.agentName }}</strong>
              <span>{{ trace.status }}</span>
            </div>
            <div class="trace-summary-meta">
              <span>{{ trace.traceKey }}</span>
              <span>{{ trace.totalLatencyMs }} ms</span>
            </div>
          </button>
        </div>

        <div class="chat-composer">
          <el-input
            v-model="chatStore.composer.message"
            type="textarea"
            :rows="4"
            placeholder="Continue the conversation..."
          />
          <div class="dialog-actions">
            <el-button @click="newSession">
              Reset
            </el-button>
            <el-button
              type="primary"
              :loading="chatStore.saving"
              @click="chatStore.sendMessage()"
            >
              Send
            </el-button>
          </div>
        </div>
      </el-card>
    </div>

    <el-dialog
      v-model="summaryVisible"
      title="Execution Summary"
      width="640px"
    >
      <div
        v-if="traceStore.activeSessionTrace"
        class="trace-step-list"
      >
        <div class="trace-summary-meta">
          <strong>{{ traceStore.activeSessionTrace.agentName }}</strong>
          <span>{{ traceStore.activeSessionTrace.status }} | {{ traceStore.activeSessionTrace.totalLatencyMs }} ms</span>
        </div>
        <article
          v-for="step in traceStore.activeSessionTrace.steps"
          :key="`${traceStore.activeSessionTrace.traceKey}-${step.stepName}-${step.summary}`"
          class="trace-summary-card trace-summary-card--detail"
        >
          <div class="trace-summary-meta">
            <strong>{{ step.stepName }}</strong>
            <span>{{ step.status }} | {{ step.latencyMs }} ms</span>
          </div>
          <p>{{ step.summary }}</p>
        </article>
      </div>
    </el-dialog>
  </section>
</template>

<script setup>
import { computed, onMounted, watch } from 'vue'

import { useAgentTraceStore } from '../stores/agentTraces'
import { useChatSessionStore } from '../stores/chatSessions'

const chatStore = useChatSessionStore()
const traceStore = useAgentTraceStore()

const summaryVisible = computed({
  get: () => Boolean(traceStore.activeSessionTrace),
  set: (value) => {
    if (!value) {
      traceStore.activeSessionTrace = null
    }
  }
})

onMounted(async () => {
  await chatStore.loadKnowledgeBaseOptions()
  await chatStore.loadSessions()
  chatStore.startNewSession('RAG')
})

watch(
  () => [chatStore.activeSession?.id, chatStore.activeSession?.sessionType],
  ([sessionId, sessionType]) => {
    if (sessionId && sessionType === 'AGENT') {
      traceStore.loadSessionTraces(sessionId)
      return
    }
    traceStore.clearSessionTraces()
  }
)

function refreshSessions() {
  chatStore.loadSessions()
}

function newSession() {
  chatStore.startNewSession(chatStore.sessionType === 'AGENT' ? 'AGENT' : 'RAG')
}

function deleteSession(session) {
  if (!window.confirm(`Delete session "${session.title}"?`)) {
    return
  }
  chatStore.deleteSession(session.id)
}

function formatUpdated(value) {
  return value ? String(value).replace('T', ' ').slice(0, 16) : ''
}
</script>

<style scoped lang="scss">
.trace-summary-section,
.trace-step-list {
  display: grid;
  gap: 12px;
  margin-bottom: 16px;
}

.trace-summary-card {
  background: #f8fafc;
  border: 1px solid #dbe4f0;
  border-radius: 12px;
  cursor: pointer;
  display: grid;
  gap: 8px;
  padding: 12px;
  text-align: left;
}

.trace-summary-card--detail {
  cursor: default;
}

.trace-summary-meta,
.section-header {
  align-items: center;
  display: flex;
  gap: 12px;
  justify-content: space-between;
}
</style>
