import request from '../utils/request'

export const getCategoryStats = () => request({ url: '/stats/category', method: 'get' })
export const getPriceRangeStats = () => request({ url: '/stats/price-range', method: 'get' })
export const getTradeTrend = () => request({ url: '/stats/trade-trend', method: 'get' })
