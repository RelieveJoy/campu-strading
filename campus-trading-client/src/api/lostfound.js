import request from '../utils/request'

export const getLostFounds = (params) => request({ url: '/lostfound', method: 'get', params })
export const getLostFoundDetail = (id) => request({ url: `/lostfound/${id}`, method: 'get' })
export const publishLostFound = (data) => request({ url: '/lostfound', method: 'post', data })
export const resolveLostFound = (id) => request({ url: `/lostfound/${id}/resolve`, method: 'put' })
