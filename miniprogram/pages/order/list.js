const { getOrderList, getOrderItems, cancelOrder, payOrder, confirmOrder } = require('../../api/order')
const { formatPrice } = require('../../utils/util')

Page({
  data: {
    orderList: [],
    currentTab: -1,
    loading: false,
    pageNum: 1,
    pageSize: 10,
    hasMore: true
  },

  onLoad(options) {
    if (options.status !== undefined && options.status !== '') {
      this.setData({ currentTab: parseInt(options.status) })
    }
    this.loadOrders()
  },

  onShow() {
    this.setData({ pageNum: 1, orderList: [], hasMore: true })
    this.loadOrders()
  },

  async loadOrders() {
    const app = getApp()
    if (!app.globalData.userId) return

    this.setData({ loading: true })
    try {
      const params = { pageNum: this.data.pageNum, pageSize: this.data.pageSize }
      if (this.data.currentTab >= 0) {
        params.status = this.data.currentTab
      }
      const res = await getOrderList(app.globalData.userId, params)
      const list = res.records || []
      
      // 加载每个订单的商品信息
      const ordersWithItems = await Promise.all(list.map(async (order) => {
        try {
          const items = await getOrderItems(order.id, app.globalData.userId)
          return {
            ...order,
            totalAmountText: formatPrice(order.totalAmount),
            payAmountText: formatPrice(order.payAmount),
            items: items || []
          }
        } catch (e) {
          return {
            ...order,
            totalAmountText: formatPrice(order.totalAmount),
            payAmountText: formatPrice(order.payAmount),
            items: []
          }
        }
      }))
      
      this.setData({
        orderList: this.data.pageNum === 1 ? ordersWithItems : [...this.data.orderList, ...ordersWithItems],
        hasMore: list.length >= this.data.pageSize
      })
    } catch (e) {
      console.error('加载订单失败', e)
    } finally {
      this.setData({ loading: false })
    }
  },

  onTabChange(e) {
    const tab = parseInt(e.currentTarget.dataset.tab)
    this.setData({ currentTab: tab, pageNum: 1, orderList: [], hasMore: true })
    this.loadOrders()
  },

  onPullDownRefresh() {
    this.setData({ pageNum: 1, orderList: [], hasMore: true })
    this.loadOrders().then(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.setData({ pageNum: this.data.pageNum + 1 })
      this.loadOrders()
    }
  },

  async onPayOrder(e) {
    const id = e.currentTarget.dataset.id
    const userId = getApp().globalData.userId
    wx.showModal({
      title: '确认支付',
      content: '确认支付该订单？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await payOrder(id, userId)
            wx.showToast({ title: '支付成功', icon: 'success' })
            this.setData({ pageNum: 1, orderList: [], hasMore: true })
            this.loadOrders()
          } catch (e) {
            wx.showToast({ title: '支付失败', icon: 'none' })
          }
        }
      }
    })
  },

  async onCancelOrder(e) {
    const id = e.currentTarget.dataset.id
    const userId = getApp().globalData.userId
    wx.showModal({
      title: '确认取消',
      content: '确定取消该订单吗？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await cancelOrder(id, userId)
            wx.showToast({ title: '已取消', icon: 'success' })
            this.setData({ pageNum: 1, orderList: [], hasMore: true })
            this.loadOrders()
          } catch (e) {
            wx.showToast({ title: '取消失败', icon: 'none' })
          }
        }
      }
    })
  },

  async onConfirmOrder(e) {
    const id = e.currentTarget.dataset.id
    const userId = getApp().globalData.userId
    wx.showModal({
      title: '确认收货',
      content: '确认已收到商品？',
      success: async (res) => {
        if (res.confirm) {
          try {
            await confirmOrder(id, userId)
            wx.showToast({ title: '已确认收货', icon: 'success' })
            this.setData({ pageNum: 1, orderList: [], hasMore: true })
            this.loadOrders()
          } catch (e) {
            wx.showToast({ title: '确认失败', icon: 'none' })
          }
        }
      }
    })
  },

  onOrderDetail(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/order/detail?id=${id}` })
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
  }
})
