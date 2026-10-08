import service from '@/utils/request'

// 获取会话列表
export function getSessionList() {
  return service.get('/psychological-chat/list')
}

// 创建新会话
export function createSession() {
  return service.post('/psychological-chat/session/start')
}

// 获取某个会话历史消息
export function getHistoryMsg(sessionId) {
  return service.get(`/psychological-chat/messages/${sessionId}`)
}
