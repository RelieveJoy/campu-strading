import request from '../utils/request'

// 获取当前用户的订单通知列表
export const getNotifications = () => request({ url: '/notifications', method: 'get' })

// 全部标记已读
export const markAllRead = () => request({ url: '/notifications/read', method: 'post' })
