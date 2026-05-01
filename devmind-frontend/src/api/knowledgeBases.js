import http from './http'

function unwrap(response) {
  return response.data.data
}

export async function fetchKnowledgeBases(params) {
  const response = await http.get('/api/knowledge-bases', { params })
  return unwrap(response)
}

export async function fetchKnowledgeBaseDetail(knowledgeBaseId) {
  const response = await http.get(`/api/knowledge-bases/${knowledgeBaseId}`)
  return unwrap(response)
}

export async function createKnowledgeBase(payload) {
  const response = await http.post('/api/knowledge-bases', payload)
  return unwrap(response)
}

export async function updateKnowledgeBase(knowledgeBaseId, payload) {
  const response = await http.put(`/api/knowledge-bases/${knowledgeBaseId}`, payload)
  return unwrap(response)
}

export async function deleteKnowledgeBase(knowledgeBaseId) {
  await http.delete(`/api/knowledge-bases/${knowledgeBaseId}`)
}
