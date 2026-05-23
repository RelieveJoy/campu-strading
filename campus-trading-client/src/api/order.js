import request from '../utils/request'

export const createOrder = (data) => request({ url: '/orders', method: 'post', data })

export const confirmOrder = (id) => request({ url: `/orders/${id}/confirm`, method: 'put' })

export const cancelOrder = (id) => request({ url: `/orders/${id}/cancel`, method: 'put' })

export const getOrders = (params) => request({ url: '/orders', method: 'get', params })

export const getOrderDetail = (id) => request({ url: `/orders/${id}`, method: 'get' })
