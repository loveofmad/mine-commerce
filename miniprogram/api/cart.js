const { get, post, put, del } = require('./request')

const getCartList = (userId) => {
  return get(`/api/cart/${userId}`)
}

const addToCart = (data) => {
  return post('/api/cart', data)
}

const updateQuantity = (id, quantity) => {
  return put(`/api/cart/${id}/quantity?quantity=${quantity}`)
}

const updateChecked = (id, checked) => {
  return put(`/api/cart/${id}/checked?checked=${checked}`)
}

const checkAll = (userId, checked) => {
  return put(`/api/cart/${userId}/checkAll?checked=${checked}`)
}

const removeCartItem = (id) => {
  return del(`/api/cart/${id}`)
}

const clearCart = (userId) => {
  return del(`/api/cart/${userId}/clear`)
}

module.exports = { getCartList, addToCart, updateQuantity, updateChecked, checkAll, removeCartItem, clearCart }
