import request from '../utils/request'

// 提交评价（rating=0 表示跳过不评）
export const submitReview = (data) =>
  request({ url: '/reviews', method: 'post', data })

// 修改评价
export const updateReview = (reviewId, data) =>
  request({ url: `/reviews/${reviewId}`, method: 'put', data })

// 商品评价列表
export const getItemReviews = (itemId) =>
  request({ url: `/reviews/item/${itemId}`, method: 'get' })

// 商品评分摘要
export const getItemRating = (itemId) =>
  request({ url: `/reviews/item/${itemId}/rating`, method: 'get' })

// 用户综合评分
export const getUserRating = (userId) =>
  request({ url: `/reviews/users/${userId}`, method: 'get' })

// 用户评价列表（他人对用户所有商品的评价）
export const getUserReviewList = (userId, params) =>
  request({ url: `/reviews/users/${userId}/list`, method: 'get', params })
