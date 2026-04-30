import { createRouter, createWebHistory } from 'vue-router'

import AppLayout from '../layouts/AppLayout.vue'
import AgentTracePage from '../views/AgentTracePage.vue'
import ChatPage from '../views/ChatPage.vue'
import DocumentStatusPage from '../views/DocumentStatusPage.vue'
import KnowledgeBasePage from '../views/KnowledgeBasePage.vue'
import LoginPage from '../views/LoginPage.vue'

const routes = [
  {
    path: '/login',
    name: 'login',
    component: LoginPage
  },
  {
    path: '/',
    component: AppLayout,
    children: [
      {
        path: '',
        redirect: '/knowledge-bases'
      },
      {
        path: '/knowledge-bases',
        name: 'knowledge-bases',
        component: KnowledgeBasePage
      },
      {
        path: '/documents',
        name: 'documents',
        component: DocumentStatusPage
      },
      {
        path: '/chat',
        name: 'chat',
        component: ChatPage
      },
      {
        path: '/agent-traces',
        name: 'agent-traces',
        component: AgentTracePage
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to) => {
  const token = window.localStorage.getItem('devmind-token')
  if (to.path !== '/login' && !token) {
    return '/login'
  }
  if (to.path === '/login' && token) {
    return '/knowledge-bases'
  }
  return true
})

export default router
