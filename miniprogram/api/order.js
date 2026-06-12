const { get, post, put, del } = require('./request')

const createOrder = (userId, addressId, orderItems, couponId, addressInfo) => {
  const params = { userId, addressId }
  if (couponId) params.couponId = couponId
  if (addressInfo) {
    params.receiverName = addressInfo.receiverName
    params.receiverPhone = addressInfo.receiverPhone
    params.receiverAddress = addressInfo.receiverAddress
  }
  return post('/api/order', orderItems, params)
}

const getOrderDetail = (id) => {
  return get(`/api/order/${id}`)
}

const getOrderList = (userId, params = {}) => {
  return get(`/api/order/list/${userId}`, params)
}

const getOrderItems = (orderId) => {
  return get(`/api/order/${orderId}/items`)
}

const cancelOrder = (id) => {
  return put(`/api/order/${id}/cancel`)
}

const payOrder = (id) => {
  return put(`/api/order/${id}/pay`)
}

const confirmOrder = (id) => {
  return put(`/api/order/${id}/confirm`)
}

const deleteOrder = (id) => {
  return del(`/api/order/${id}`)
}

module.exports = { createOrder, getOrderDetail, getOrderList, getOrderItems, cancelOrder, payOrder, confirmOrder, deleteOrder }
