import http from './http'

function unwrap(response) {
  return response.data.data
}

export async function fetchModelConfigurationOverview() {
  const response = await http.get('/api/admin/model-configurations/overview')
  return unwrap(response)
}

export async function fetchModelConfigurations() {
  const response = await http.get('/api/admin/model-configurations')
  return unwrap(response)
}

export async function createModelConfiguration(payload) {
  const response = await http.post('/api/admin/model-configurations', payload)
  return unwrap(response)
}

export async function updateModelConfiguration(configId, payload) {
  const response = await http.put(`/api/admin/model-configurations/${configId}`, payload)
  return unwrap(response)
}

export async function updateModelConfigurationEnabled(configId, enabled) {
  const response = await http.patch(`/api/admin/model-configurations/${configId}/enabled`, { enabled })
  return unwrap(response)
}

export async function setDefaultChatConfiguration(configId) {
  const response = await http.patch(`/api/admin/model-configurations/${configId}/default-chat`)
  return unwrap(response)
}

export async function setDefaultEmbeddingConfiguration(configId) {
  const response = await http.patch(`/api/admin/model-configurations/${configId}/default-embedding`)
  return unwrap(response)
}
