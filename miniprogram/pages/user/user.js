const { getUserInfo, login, register } = require('../../api/user')
const { setUserInfo, clearUserInfo } = require('../../utils/util')

Page({
  data: {
    userInfo: null,
    isLoggedIn: false,
    showLogin: false,
    loginType: 'login',
    username: '',
    password: '',
    phone: ''
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

  onLoginTap() {
    this.setData({ showLogin: true, loginType: 'login' })
  },

  onRegisterTap() {
    this.setData({ showLogin: true, loginType: 'register' })
  },

  onCloseLogin() {
    this.setData({ showLogin: false })
  },

  onSwitchType() {
    this.setData({
      loginType: this.data.loginType === 'login' ? 'register' : 'login'
    })
  },

  onUsernameInput(e) {
    this.setData({ username: e.detail.value })
  },

  onPasswordInput(e) {
    this.setData({ password: e.detail.value })
  },

  onPhoneInput(e) {
    this.setData({ phone: e.detail.value })
  },

  async onSubmitLogin() {
    const { username, password, phone, loginType } = this.data
    if (!username || !password) {
      wx.showToast({ title: '请填写用户名和密码', icon: 'none' })
      return
    }
    try {
      let user
      if (loginType === 'login') {
        user = await login(username, password)
      } else {
        if (!phone) {
          wx.showToast({ title: '请填写手机号', icon: 'none' })
          return
        }
        user = await register(username, password, phone)
      }
      const app = getApp()
      app.globalData.userId = user.id
      app.globalData.userInfo = user
      setUserInfo(user)
      this.setData({
        isLoggedIn: true,
        userInfo: user,
        showLogin: false,
        username: '',
        password: '',
        phone: ''
      })
      wx.showToast({ title: '登录成功', icon: 'success' })
    } catch (e) {
      wx.showToast({ title: '操作失败', icon: 'none' })
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
