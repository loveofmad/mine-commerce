const { getCartList, updateQuantity, updateChecked, checkAll, removeCartItem } = require('../../api/cart')
const { formatPrice, checkLogin } = require('../../utils/util')

Page({
  data: {
    cartList: [],
    allChecked: false,
    totalPrice: '0.00',
    totalCount: 0,
    isEdit: false
  },

  onShow() {
    if (getApp().globalData.userId) {
      this.loadCart()
    } else {
      this.setData({ cartList: [] })
    }
  },

  async loadCart() {
    try {
      console.log('开始加载购物车, userId:', getApp().globalData.userId)
      const list = await getCartList(getApp().globalData.userId)
      console.log('购物车数据:', list)
      const baseUrl = getApp().globalData.baseUrl
      const fixImage = (img) => img && img.startsWith('/') ? baseUrl + img : img
      const cartList = list.map(item => ({
        ...item,
        skuImage: fixImage(item.skuImage),
        priceText: formatPrice(item.price)
      }))
      this.setData({ cartList })
      this.calcTotal()
    } catch (e) {
      console.error('loadCart error', e)
    }
  },

  calcTotal() {
    let totalPrice = 0
    let totalCount = 0
    let allChecked = true
    this.data.cartList.forEach(item => {
      if (item.checked) {
        totalPrice += item.price * item.quantity
        totalCount += item.quantity
      } else {
        allChecked = false
      }
    })
    this.setData({
      totalPrice: formatPrice(totalPrice),
      totalCount,
      allChecked: this.data.cartList.length > 0 && allChecked
    })
  },

  async onCheckTap(e) {
    const index = e.currentTarget.dataset.index
    const item = this.data.cartList[index]
    const checked = item.checked ? 0 : 1
    const userId = getApp().globalData.userId
    try {
      await updateChecked(item.id, checked, userId)
      this.setData({ [`cartList[${index}].checked`]: checked })
      this.calcTotal()
    } catch (e) {
      console.error(e)
    }
  },

  async onCheckAllTap() {
    const checked = this.data.allChecked ? 0 : 1
    try {
      await checkAll(getApp().globalData.userId, checked)
      const cartList = this.data.cartList.map(item => ({ ...item, checked }))
      this.setData({ cartList, allChecked: !!checked })
      this.calcTotal()
    } catch (e) {
      console.error(e)
    }
  },

  async onQuantityMinus(e) {
    const index = e.currentTarget.dataset.index
    const item = this.data.cartList[index]
    if (item.quantity <= 1) return
    const userId = getApp().globalData.userId
    try {
      await updateQuantity(item.id, item.quantity - 1, userId)
      this.setData({ [`cartList[${index}].quantity`]: item.quantity - 1 })
      this.calcTotal()
    } catch (e) {
      console.error(e)
    }
  },

  async onQuantityPlus(e) {
    const index = e.currentTarget.dataset.index
    const item = this.data.cartList[index]
    const userId = getApp().globalData.userId
    try {
      await updateQuantity(item.id, item.quantity + 1, userId)
      this.setData({ [`cartList[${index}].quantity`]: item.quantity + 1 })
      this.calcTotal()
    } catch (e) {
      console.error(e)
    }
  },

  async onDeleteTap(e) {
    const index = e.currentTarget.dataset.index
    const item = this.data.cartList[index]
    const userId = getApp().globalData.userId
    wx.showModal({
      title: '提示',
      content: '确定删除该商品吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await removeCartItem(item.id, userId)
            const cartList = this.data.cartList.filter((_, i) => i !== index)
            this.setData({ cartList })
            this.calcTotal()
            wx.showToast({ title: '已删除', icon: 'success' })
          } catch (e) {
            console.error(e)
          }
        }
      }
    })
  },

  onEditTap() {
    this.setData({ isEdit: !this.data.isEdit })
  },

  goHome() {
    wx.switchTab({ url: '/pages/index/index' })
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
  },

  onCheckout() {
    if (!checkLogin()) return
    if (this.data.totalCount === 0) {
      wx.showToast({ title: '请选择商品', icon: 'none' })
      return
    }
    const items = this.data.cartList.filter(item => item.checked).map(item => ({
      cartId: item.id,
      spuId: item.spuId,
      skuId: item.skuId,
      spuName: item.spuName,
      skuTitle: item.skuTitle,
      skuImage: item.skuImage,
      price: item.price,
      quantity: item.quantity
    }))
    wx.navigateTo({
      url: `/pages/order/order?items=${encodeURIComponent(JSON.stringify(items))}&fromCart=1`
    })
  }
})
