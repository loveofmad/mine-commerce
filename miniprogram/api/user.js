const { get, post, put } = require('./request')

const wxLogin = (code) => {
  return post('/api/user/wx-login', { code })
}

const getUserInfo = (id) => {
  return get(`/api/user/${id}`)
}

const updateUserInfo = (data) => {
  return put('/api/user', data)
}

module.exports = { wxLogin, getUserInfo, updateUserInfo }
