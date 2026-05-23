import request from '../utils/request'

export const addFavorite = (data) => request({ url: '/favorites', method: 'post', data })

export const deleteFavorite = (id) => request({ url: `/favorites/${id}`, method: 'delete' })

export const getFavorites = () => request({ url: '/favorites', method: 'get' })

export const checkFavorite = (itemId) => request({ url: '/favorites/check', method: 'get', params: { itemId } })
