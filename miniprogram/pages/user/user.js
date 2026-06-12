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
      this.setData({ userInfo: user })
      getApp().globalData.userInfo = user
      setUserInfo(user)
    } catch (e) {
      console.error(e)
    }
  },

  async onLoginTap() {
    try {
      wx.showLoading({ title: '登录中...' })
      
      // 尝试获取微信code，模拟器可能失败
      let code = 'dev_code_' + Date.now() // 开发环境使用模拟code
      try {
        const loginRes = await new Promise((resolve, reject) => {
          wx.login({ success: resolve, fail: reject })
        })
        if (loginRes.code) {
          code = loginRes.code
        }
      } catch (e) {
        console.log('wx.login失败，使用模拟code:', e)
      }
      
      const user = await wxLogin(code)
      const app = getApp()
      app.globalData.userId = user.id
      app.globalData.userInfo = user
      setUserInfo(user)
      this.setData({
        isLoggedIn: true,
        userInfo: user
      })
      wx.hideLoading()
      wx.showToast({ title: '登录成功', icon: 'success' })
    } catch (e) {
      wx.hideLoading()
      wx.showToast({ title: '登录失败', icon: 'none' })
      console.error('登录失败:', e)
    }
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
    wx.navigateTo({ url: `/pages/user/user?tab=order&status=${status}` })
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
