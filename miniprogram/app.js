App({
  onLaunch() {
    const userInfo = wx.getStorageSync('userInfo')
    if (userInfo) {
      this.globalData.userInfo = userInfo
      this.globalData.userId = userInfo.id
    }
  },
  globalData: {
    userInfo: null,
    userId: null,
    // 开发工具使用 localhost，真机/模拟器使用电脑IP
    baseUrl: 'http://10.32.90.79:8080'
  }
})
