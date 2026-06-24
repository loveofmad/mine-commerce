const { getUserInfo, wxLogin } = require('../../api/user')
const { setUserInfo, clearUserInfo } = require('../../utils/util')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false
  },

  onShow() {
    const app = getApp()
    if (app.globalData.userId) {
      this.setData({ isLoggedIn: true, userInfo: app.globalData.userInfo })
      this.loadUserInfo(app.globalData.userId)
    } else {
      this.setData({ isLoggedIn: false, userInfo: null })
    }
  },

  async loadUserInfo(userId) {
    try {
      const user = await getUserInfo(userId)
      const baseUrl = getApp().globalData.baseUrl
      if (user.avatar && user.avatar.startsWith('/')) {
        user.avatar = baseUrl + user.avatar
      }
      this.setData({ userInfo: user })
      getApp().globalData.userInfo = user
      setUserInfo(user)
    } catch (e) {
      console.error(e)
    }
  },

  onLoginTap() {
    wx.showModal({
      title: '提示',
      content: '是否使用默认账号登录？',
      success: (res) => {
        if (res.confirm) {
          const app = getApp()
          const userInfo = { id: 1, username: 'user1', nickname: '测试用户1', phone: '13800138001' }
          app.globalData.userId = 1
          app.globalData.userInfo = userInfo
          wx.setStorageSync('token', 'user-demo-token')
          wx.setStorageSync('userInfo', userInfo)
          this.setData({
            isLoggedIn: true,
            userInfo
          })
          this.loadUserInfo(1)
          wx.showToast({ title: '登录成功', icon: 'success' })
        }
      }
    })
  },

  onLogout() {
    wx.showModal({
      title: '提示',
      content: '确定退出登录吗？',
      success: (res) => {
        if (res.confirm) {
          const app = getApp()
          app.globalData.userId = null
          app.globalData.userInfo = null
          clearUserInfo()
          this.setData({ isLoggedIn: false, userInfo: null })
          wx.showToast({ title: '已退出', icon: 'success' })
        }
      }
    })
  },

  onOrderTap(e) {
    const status = e.currentTarget.dataset.status || ''
    wx.navigateTo({ url: `/pages/order/list?status=${status}` })
  },

  onMenuItemTap(e) {
    const url = e.currentTarget.dataset.url
    if (url) {
      wx.navigateTo({ url })
    } else {
      wx.showToast({ title: '功能开发中', icon: 'none' })
    }
  }
})
