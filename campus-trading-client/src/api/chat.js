import request from '../utils/request'

// 获取某个商品下与对方的聊天记录
export const getMessages = (itemId, receiverId) => request({ url: `/messages/${itemId}`, method: 'get', params: { receiverId } })

// 发送消息
export const sendMessage = (data) => request({ url: '/messages', method: 'post', data })

// 获取对话列表
export const getConversations = () => request({ url: '/messages/conversations', method: 'get' })

// 标记已读
export const markRead = (itemId) => request({ url: `/messages/${itemId}/read`, method: 'post' })
