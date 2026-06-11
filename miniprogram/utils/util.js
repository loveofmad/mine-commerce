const formatPrice = (price) => {
  if (!price) return '0.00'
  return parseFloat(price).toFixed(2)
}

const formatDate = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  return `${y}-${m}-${d}`
}

const formatDateTime = (dateStr) => {
  if (!dateStr) return ''
  const date = new Date(dateStr)
  const y = date.getFullYear()
  const m = String(date.getMonth() + 1).padStart(2, '0')
  const d = String(date.getDate()).padStart(2, '0')
  const h = String(date.getHours()).padStart(2, '0')
  const min = String(date.getMinutes()).padStart(2, '0')
  return `${y}-${m}-${d} ${h}:${min}`
}

const getUserInfo = () => {
  return wx.getStorageSync('userInfo') || null
}

const setUserInfo = (info) => {
  wx.setStorageSync('userInfo', info)
}

const clearUserInfo = () => {
  wx.removeStorageSync('userInfo')
}

const checkLogin = () => {
  const app = getApp()
  if (!app.globalData.userId) {
    wx.showModal({
      title: '提示',
      content: '请先登录',
      confirmText: '去登录',
      success(res) {
        if (res.confirm) {
          wx.navigateTo({ url: '/pages/user/user' })
        }
      }
    })
    return false
  }
  return true
}

module.exports = { formatPrice, formatDate, formatDateTime, getUserInfo, setUserInfo, clearUserInfo, checkLogin }
