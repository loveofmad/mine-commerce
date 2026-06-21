const { get, post, put, del } = require('./request')

const createOrder = (userId, addressId, orderItems, couponId, addressInfo, remark) => {
  const params = { userId, addressId }
  if (couponId) params.couponId = couponId
  if (remark) params.remark = remark
  if (addressInfo) {
    params.receiverName = addressInfo.receiverName
    params.receiverPhone = addressInfo.receiverPhone
    params.receiverAddress = addressInfo.receiverAddress
  }
  return post('/api/order', orderItems, params)
}

const getOrderDetail = (id, userId) => {
  return get(`/api/order/${id}`, { userId })
}

const getOrderList = (userId, params = {}) => {
  return get(`/api/order/list/${userId}`, params)
}

const getOrderItems = (orderId, userId) => {
  return get(`/api/order/${orderId}/items`, { userId })
}

const cancelOrder = (id, userId) => {
  return put(`/api/order/${id}/cancel`, null, { userId })
}

const payOrder = (id, userId) => {
  return put(`/api/order/${id}/pay`, null, { userId })
}

const confirmOrder = (id, userId) => {
  return put(`/api/order/${id}/confirm`, null, { userId })
}

const deleteOrder = (id, userId) => {
  return del(`/api/order/${id}`, null, { userId })
}

module.exports = { createOrder, getOrderDetail, getOrderList, getOrderItems, cancelOrder, payOrder, confirmOrder, deleteOrder }
