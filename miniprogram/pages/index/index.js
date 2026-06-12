const { getProductList } = require('../../api/product')
const { formatPrice } = require('../../utils/util')

Page({
  data: {
    banners: [
      { id: 1, imageUrl: '/static/banner1.png', title: '精选土特产' },
      { id: 2, imageUrl: '/static/banner2.png', title: '新鲜水果' },
      { id: 3, imageUrl: '/static/banner3.png', title: '地道美味' }
    ],
    categories: [
      { id: 1, name: '水果', icon: '/static/icon-fruit.png' },
      { id: 2, name: '坚果', icon: '/static/icon-nuts.png' },
      { id: 3, name: '茶叶', icon: '/static/icon-tea.png' },
      { id: 4, name: '蜂蜜', icon: '/static/icon-honey.png' },
      { id: 5, name: '特产礼盒', icon: '/static/icon-gift.png' }
    ],
    productList: [],
    pageNum: 1,
    pageSize: 10,
    hasMore: true,
    loading: false
  },

  onLoad() {
    this.loadProducts()
  },

  onPullDownRefresh() {
    this.setData({ pageNum: 1, productList: [], hasMore: true })
    this.loadProducts().then(() => wx.stopPullDownRefresh())
  },

  onReachBottom() {
    if (this.data.hasMore && !this.data.loading) {
      this.loadProducts()
    }
  },

  async loadProducts() {
    if (this.data.loading) return
    this.setData({ loading: true })
    try {
      console.log('开始加载商品...')
      const res = await getProductList({
        pageNum: this.data.pageNum,
        pageSize: this.data.pageSize
      })
      console.log('API返回:', res)
      const list = res.records || []
      const app = getApp()
      const baseUrl = app.globalData.baseUrl
      console.log('商品列表:', list)
      this.setData({
        productList: [...this.data.productList, ...list.map(item => ({
          ...item,
          mainImage: item.mainImage && item.mainImage.startsWith('/') ? baseUrl + item.mainImage : item.mainImage,
          priceText: formatPrice(item.price)
        }))],
        pageNum: this.data.pageNum + 1,
        hasMore: list.length >= this.data.pageSize,
        loading: false
      })
    } catch (e) {
      console.error('加载商品失败:', e)
      this.setData({ loading: false })
    }
  },

  onBannerTap(e) {
    const id = e.currentTarget.dataset.id
    if (id) {
      wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
    }
  },

  onCategoryTap(e) {
    const id = e.currentTarget.dataset.id
    wx.switchTab({ url: '/pages/category/category' })
  },

  onProductTap(e) {
    const id = e.currentTarget.dataset.id
    wx.navigateTo({ url: `/pages/detail/detail?id=${id}` })
  },

  onSearchTap() {
    wx.navigateTo({ url: '/pages/category/category' })
  }
})
