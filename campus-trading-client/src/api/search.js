// TODO: 后端接口待开发 — 搜索建议/历史
import request from '../utils/request'

export const getSearchSuggestions = (q) => request({ url: '/search/suggestions', method: 'get', params: { q } })
export const clearSearchHistory = () => request({ url: '/search/history', method: 'delete' })
