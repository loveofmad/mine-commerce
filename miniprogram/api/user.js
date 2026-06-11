const { get, post, put } = require('./request')

const login = (username, password) => {
  return post('/api/user/login', { username, password })
}

const register = (username, password, phone) => {
  return post('/api/user/register', { username, password, phone })
}

const getUserInfo = (id) => {
  return get(`/api/user/${id}`)
}

const updateUserInfo = (data) => {
  return put('/api/user', data)
}

module.exports = { login, register, getUserInfo, updateUserInfo }
