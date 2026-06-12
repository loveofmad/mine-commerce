const { get, post, put, del } = require('./request')

const getAddressList = (userId) => {
  return get('/api/address/list', { userId })
}

const addAddress = (data) => {
  return post('/api/address', data)
}

const updateAddress = (id, data) => {
  return put(`/api/address/${id}`, data)
}

const deleteAddress = (id) => {
  return del(`/api/address/${id}`)
}

module.exports = { getAddressList, addAddress, updateAddress, deleteAddress }
