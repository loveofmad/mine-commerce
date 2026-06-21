const { getOrderDetail, getOrderItems, cancelOrder, payOrder, confirmOrder } = require('../../api/order')
const { formatPrice } = require('../../utils/util')

Page({
  data: {
    order: null,
    orderItems: [],
    loading: false
  },

  onLoad(options) {
    if (options.id) {
      this.loadOrderDetail(options.id)
    }
  },

  async loadOrderDetail(id) {
    this.setData({ loading: true })
    const userId = getApp().globalData.userId
    try {
      const [order, items] = await Promise.all([
        getOrderDetail(id, userId),
        getOrderItems(id, userId)
      ])
      this.setData({
        order: {
          ...order,
          totalAmountText: formatPrice(order.totalAmount),
          payAmountText: formatPrice(order.payAmount)
        },
        orderItems: items || []
      })
    } catch (e) {
      wx.showToast({ title: '加载失败', icon: 'none' })
    } finally {
      this.setData({ loading: false })
    }
  },

  async onPayOrder() {
    const id = this.data.order.id
    const userId = getApp().globalData.userId
    wx.showModal({
      title: '确认支付',
      content: '确认支付该订单？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await payOrder(id, userId)
            wx.showToast({ title: '支付成功', icon: 'success' })
            this.loadOrderDetail(id)
          } catch (e) {
            wx.showToast({ title: '支付失败', icon: 'none' })
          }
        }
      }
    })
  },

  async onCancelOrder() {
    const id = this.data.order.id
    const userId = getApp().globalData.userId
    wx.showModal({
      title: '确认取消',
      content: '确定取消该订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await cancelOrder(id, userId)
            wx.showToast({ title: '已取消', icon: 'success' })
            this.loadOrderDetail(id)
          } catch (e) {
            wx.showToast({ title: '取消失败', icon: 'none' })
          }
        }
      }
    })
  },

  async onConfirmOrder() {
    const id = this.data.order.id
    const userId = getApp().globalData.userId
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await confirmOrder(id, userId)
            wx.showToast({ title: '已确认收货', icon: 'success' })
            this.loadOrderDetail(id)
          } catch (e) {
            wx.showToast({ title: '确认失败', icon: 'none' })
          }
        }
      }
    })
  },

  getStatusText(status) {
    const map = { 0: '待付款', 1: '待发货', 2: '待收货', 3: '已完成', 4: '已取消' }
    return map[status] || '未知'
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
  }
})
