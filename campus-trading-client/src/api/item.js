import request from '../utils/request'

export const addItem = (data) => request({ url: '/items', method: 'post', data })

export const updateItem = (id, data) => request({ url: `/items/${id}`, method: 'put', data })

export const deleteItem = (id) => request({ url: `/items/${id}`, method: 'delete' })

export const relistItem = (id) => request({ url: `/items/${id}/relist`, method: 'put' })

export const getItems = (params) => request({ url: '/items', method: 'get', params })

export const getHomeItems = () => request({ url: '/items/home', method: 'get' })

export const getItemDetail = (id) => request({ url: `/items/${id}`, method: 'get' })

export const uploadFile = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request({ url: '/common/upload', method: 'post', data: formData, headers: { 'Content-Type': 'multipart/form-data' } })
}
