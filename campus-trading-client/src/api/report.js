import request from '../utils/request'

export const submitReport = (data) => request({ url: '/reports', method: 'post', data })

export const getReports = (params) => request({ url: '/reports', method: 'get', params })

export const resolveReport = (id) => request({ url: `/reports/${id}/resolve`, method: 'put' })
