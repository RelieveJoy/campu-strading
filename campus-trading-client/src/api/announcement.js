// TODO: 后端接口待开发 — 公告栏
import request from '../utils/request'

export const getAnnouncements = () => request({ url: '/announcements', method: 'get' })
