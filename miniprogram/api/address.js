const { get, post, put, del } = require('./request')

const getAddressList = (userId) => {
  return get('/api/address/list', { userId })
}

const addAddress = (data) => {
  return post('/api/address', data)
}

const updateAddress = (id, data, userId) => {
  return put(`/api/address/${id}?userId=${userId}`, data)
}

const deleteAddress = (id, userId) => {
  return del(`/api/address/${id}?userId=${userId}`)
}

module.exports = { getAddressList, addAddress, updateAddress, deleteAddress }
