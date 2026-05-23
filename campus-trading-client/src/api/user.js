import request from '../utils/request'

export const login = (data) => request({ url: '/users/login', method: 'post', data })

export const register = (data) => request({ url: '/users/register', method: 'post', data })

export const logout = () => request({ url: '/users/logout', method: 'post' })

export const getUserInfo = (id) => request({ url: `/users/${id}`, method: 'get' })

export const updateUserInfo = (id, data) => request({ url: `/users/${id}`, method: 'put', data })

export const editPassword = (data) => request({ url: '/users/password', method: 'put', data })

export const getUserItems = (id) => request({ url: `/users/${id}/items`, method: 'get' })
