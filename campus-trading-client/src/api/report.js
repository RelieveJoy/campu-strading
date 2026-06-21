// TODO: 后端接口待开发 — 举报
import request from '../utils/request'

export const submitReport = (data) => request({ url: '/reports', method: 'post', data })
