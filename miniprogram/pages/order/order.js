const { createOrder } = require('../../api/order')
const { getAddressList } = require('../../api/address')
const { formatPrice } = require('../../utils/util')

Page({
  data: {
    items: [],
    address: null,
    remark: '',
    totalAmount: '0.00',
    submitting: false,
    showAddressPicker: false,
    addressList: []
  },

  onLoad(options) {
    if (options.items) {
      try {
        const items = JSON.parse(decodeURIComponent(options.items))
        const totalAmount = items.reduce((sum, item) => sum + item.price * item.quantity, 0)
        this.setData({
          items: items.map(item => ({
            ...item,
            priceText: formatPrice(item.price),
            totalPriceText: formatPrice(item.price * item.quantity)
          })),
          totalAmount: formatPrice(totalAmount)
        })
      } catch (e) {
        wx.showToast({ title: '参数错误', icon: 'none' })
      }
    }

    const savedAddress = wx.getStorageSync('defaultAddress')
    if (savedAddress) {
      this.setData({ address: savedAddress })
    }
  },

  showAddressPicker() {
    const app = getApp()
    if (!app.globalData.userId) {
      wx.showToast({ title: '请先登录', icon: 'none' })
      return
    }
    this.loadAddressList()
    this.setData({ showAddressPicker: true })
  },

  closeAddressPicker() {
    this.setData({ showAddressPicker: false })
  },

  async loadAddressList() {
    const app = getApp()
    try {
      const res = await getAddressList(app.globalData.userId)
      this.setData({ addressList: res || [] })
    } catch (e) {
      console.error('加载地址失败', e)
    }
  },

  selectAddress(e) {
    const address = e.currentTarget.dataset.address
    this.setData({ address, showAddressPicker: false })
    wx.setStorageSync('defaultAddress', address)
  },

  goToAddAddress() {
    this.setData({ showAddressPicker: false })
    wx.navigateTo({ url: '/pages/address/address' })
  },

  onRemarkInput(e) {
    this.setData({ remark: e.detail.value })
  },

  async onSubmitOrder() {
    if (!this.data.address) {
      wx.showToast({ title: '请选择收货地址', icon: 'none' })
      return
    }
    if (this.data.items.length === 0) {
      wx.showToast({ title: '无订单商品', icon: 'none' })
      return
    }
    if (this.data.submitting) return
    this.setData({ submitting: true })

    const app = getApp()
    const orderItems = this.data.items.map(item => ({
      spuId: item.spuId,
      skuId: item.skuId,
      spuName: item.spuName,
      skuTitle: item.skuTitle,
      skuImage: item.skuImage,
      price: item.price,
      quantity: item.quantity
    }))

    // 保存地址信息到订单
    const addressInfo = {
      receiverName: this.data.address.name,
      receiverPhone: this.data.address.phone,
      receiverAddress: this.data.address.province + this.data.address.city + this.data.address.district + this.data.address.detail
    }

    try {
      const order = await createOrder(app.globalData.userId, 0, orderItems, null, addressInfo)
      wx.showToast({ title: '下单成功', icon: 'success' })
      setTimeout(() => {
        wx.redirectTo({ url: `/pages/order/detail?id=${order.id}` })
      }, 1500)
    } catch (e) {
      wx.showToast({ title: '下单失败', icon: 'none' })
    } finally {
      this.setData({ submitting: false })
    }
  }
})
