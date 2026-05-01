import http from './http'

function unwrap(response) {
  return response.data.data
}

export async function fetchChatSessions(params) {
  const response = await http.get('/api/chat/sessions', { params })
  return unwrap(response)
}

export async function fetchChatSessionDetail(sessionId) {
  const response = await http.get(`/api/chat/sessions/${sessionId}`)
  return unwrap(response)
}

export async function deleteChatSession(sessionId) {
  await http.delete(`/api/chat/sessions/${sessionId}`)
}

export async function askRag(payload) {
  const response = await http.post('/api/rag/ask', payload)
  return unwrap(response)
}

export async function executeAgent(payload) {
  const response = await http.post('/api/agents/execute', payload)
  return unwrap(response)
}
