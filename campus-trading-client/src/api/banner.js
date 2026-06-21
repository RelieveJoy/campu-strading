// TODO: 后端接口待开发 — 首页轮播图
import request from '../utils/request'

export const getBanners = () => request({ url: '/banners', method: 'get' })
